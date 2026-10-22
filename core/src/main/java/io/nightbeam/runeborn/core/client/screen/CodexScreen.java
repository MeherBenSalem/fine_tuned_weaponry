package io.nightbeam.runeborn.core.client.screen;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.RbScreen;
import io.nightbeam.runeborn.core.item.Recipes;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** In-game guide: chapters on the left, a scrolling page with recipes and references on the right. */
public final class CodexScreen extends RbScreen {
    private static final int KEY_ESCAPE = 256;
    private static final int CELL = 18;
    private static final int ROW_H = 15;

    private enum Chapter {
        RUNES(Lang.CX_RUNES, Lang.CX_RUNES_BODY),
        SHRINES(Lang.CX_SHRINES, Lang.CX_SHRINES_BODY),
        ATTRIBUTES(Lang.CX_ATTRS, Lang.CX_ATTRS_BODY),
        FLASKS(Lang.CX_FLASKS, Lang.CX_FLASKS_BODY),
        ARMAMENTS(Lang.CX_ARMAMENTS, Lang.CX_ARMAMENTS_BODY),
        ARTS(Lang.CX_ARTS, Lang.CX_ARTS_BODY),
        COMBAT(Lang.CX_COMBAT, Lang.CX_COMBAT_BODY),
        WORLD(Lang.CX_WORLD, Lang.CX_WORLD_BODY),
        SOVEREIGNS(Lang.CX_SOVEREIGNS, Lang.CX_SOVEREIGNS_BODY),
        CONTROLS(Lang.CX_CONTROLS, Lang.CX_CONTROLS_BODY);

        final String title;
        final String body;

        Chapter(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    private static Chapter lastChapter = Chapter.RUNES;

    private final RbScreen back;
    private Chapter chapter = lastChapter;
    private int left;
    private int top;
    private int listW;
    private int pageW;
    private int panelH;
    private float scroll;
    private int contentH;
    /** Item under the mouse this frame; its tooltip is drawn after the page is clipped. */
    private String hoverItem;

    /** @param back screen to return to, or null to close */
    public CodexScreen(RbScreen back) {
        this.back = back;
    }

    @Override
    protected void init() {
        listW = 112;
        pageW = Math.max(220, Math.min(360, width - listW - 48));
        int total = listW + 8 + pageW;
        left = width / 2 - total / 2;
        panelH = Math.max(120, Math.min(height - 80, 250));
        top = Math.max(40, height / 2 - panelH / 2 + 6);
    }

    @Override
    public void render(Canvas c, int mx, int my, float partial) {
        clearHotspots();
        hoverItem = null;
        Kit.backdrop(c);
        int total = listW + 8 + pageW;
        c.title(c.tr(Lang.CODEX).toUpperCase(Locale.ROOT), left, top - 28, Theme.GOLD_BRIGHT, 1.6f);
        Ui.right(c, c.tr(Lang.CODEX_HINT), left + total, top - 22, Theme.TEXT_FAINT, false);
        Ui.rule(c, left, top - 10, total, Theme.alpha(Theme.GOLD, 0.7f));

        // Chapter list
        Ui.panel(c, left, top, listW, panelH);
        int y = top + 6;
        for (Chapter ch : Chapter.values()) {
            boolean selected = ch == chapter;
            boolean hover = Ui.hit(mx, my, left + 2, y - 3, listW - 4, ROW_H - 1);
            if (selected) {
                Ui.band(c, left + 2, y - 3, listW - 4, ROW_H - 1, Theme.ROW_SELECTED);
            } else if (hover) {
                Ui.band(c, left + 2, y - 3, listW - 4, ROW_H - 1, Theme.ROW_HOVER);
            }
            Ui.diamond(c, left + 9, y + 3, 2, selected ? Theme.GOLD_BRIGHT : Theme.GOLD_DIM);
            c.text(c.tr(ch.title), left + 16, y, selected ? Theme.GOLD_BRIGHT : Theme.TEXT, false);
            hot(left + 2, y - 3, listW - 4, ROW_H - 1, () -> select(ch));
            y += ROW_H;
        }
        int backY = top + panelH - 22;
        boolean backHover = Ui.hit(mx, my, left + 8, backY, listW - 16, 16);
        Ui.button(c, left + 8, backY, listW - 16, 16, c.tr(back == null ? Lang.CLOSE : Lang.BACK), backHover, true);
        hot(left + 8, backY, listW - 16, 16, this::leave);

        // Page
        int px = left + listW + 8;
        Ui.panel(c, px, top, pageW, panelH);
        int viewTop = top + 3;
        int viewBottom = top + panelH - 3;
        boolean inView = Ui.hit(mx, my, px, viewTop, pageW, viewBottom - viewTop);
        c.scissor(px + 1, viewTop, px + pageW - 1, viewBottom);
        int startY = top + 10 - Math.round(scroll);
        int endY = page(c, px + 12, startY, pageW - 24, inView ? mx : -1, inView ? my : -1);
        c.endScissor();
        contentH = endY - startY + 10;
        int viewH = viewBottom - viewTop;
        scroll = Math.max(0, Math.min(scroll, Math.max(0, contentH - viewH)));
        if (contentH > viewH) {
            int trackH = viewH - 4;
            int thumbH = Math.max(16, trackH * viewH / contentH);
            int thumbY = viewTop + 2 + Math.round((trackH - thumbH) * (scroll / (contentH - viewH)));
            c.fill(px + pageW - 4, viewTop + 2, px + pageW - 2, viewTop + 2 + trackH, Theme.BORDER_DIM);
            c.fill(px + pageW - 4, thumbY, px + pageW - 2, thumbY + thumbH, Theme.GOLD_DIM);
        }
        if (hoverItem != null) {
            c.itemTooltip(hoverItem, mx, my);
        }
    }

    private void select(Chapter ch) {
        chapter = ch;
        lastChapter = ch;
        scroll = 0;
    }

    private void leave() {
        if (back != null) {
            host.open(back);
        } else {
            host.close();
        }
    }

    // ------------------------------------------------------------------ pages

    /** Draws the current chapter and returns the y below it. */
    private int page(Canvas c, int x, int y, int w, int mx, int my) {
        c.title(c.tr(chapter.title).toUpperCase(Locale.ROOT), x, y, Theme.GOLD, 1.25f);
        Ui.rule(c, x, y + 14, w, Theme.alpha(Theme.GOLD, 0.5f));
        y += 22;
        for (String para : c.tr(chapter.body).split("\n")) {
            y = Ui.paragraph(c, para, x, y, w, Theme.TEXT_DIM) + 4;
        }
        y += 2;
        return switch (chapter) {
            case SHRINES -> recipe(c, Rb.id("ember_shrine"), x, y, w, mx, my);
            case ATTRIBUTES -> attributes(c, x, y, w);
            case FLASKS -> {
                int ny = recipe(c, Rb.id("ember_flask"), x, y, w, mx, my);
                yield recipe(c, Rb.id("azure_flask"), x, ny, w, mx, my);
            }
            case ARMAMENTS -> {
                int ny = recipe(c, Rb.id("tempering_stone_1"), x, y, w, mx, my);
                ny = recipe(c, Rb.id("blank_whetstone"), x, ny, w, mx, my);
                yield whetstones(c, x, ny, w, mx, my);
            }
            case SOVEREIGNS -> sovereigns(c, x, y, w, mx, my);
            case CONTROLS -> controls(c, x, y, w);
            default -> y;
        };
    }

    private int recipe(Canvas c, String result, int x, int y, int w, int mx, int my) {
        Recipes.Recipe r = Recipes.forResult(result);
        if (r == null) {
            return y;
        }
        Kit.sectionLabel(c, c.tr(Lang.CODEX_RECIPE) + " §8·§r " + itemName(c, result), x, y, w);
        y += 15;
        String[] grid = r.grid();
        for (int i = 0; i < 9; i++) {
            int cx = x + (i % 3) * CELL;
            int cy = y + (i / 3) * CELL;
            cell(c, cx, cy, grid[i], mx, my, Theme.BORDER_DIM);
        }
        int ax = x + CELL * 3 + 8;
        int ay = y + CELL + 4;
        c.text("→", ax, ay, Theme.GOLD_DIM, false);
        int rx = ax + 16;
        int ry = y + CELL - 2;
        c.fill(rx, ry, rx + 22, ry + 22, 0xC0171410);
        Ui.frame(c, rx, ry, 22, 22, Theme.GOLD_DIM);
        c.item(result, rx + 3, ry + 3);
        if (r.count() > 1) {
            String n = String.valueOf(r.count());
            c.text(n, rx + 20 - c.textWidth(n), ry + 13, Theme.TEXT, true);
        }
        if (Ui.hit(mx, my, rx, ry, 22, 22)) {
            hoverItem = result;
        }
        return y + CELL * 3 + 8;
    }

    private void cell(Canvas c, int x, int y, String item, int mx, int my, int border) {
        c.fill(x, y, x + CELL, y + CELL, 0xA0100E0C);
        Ui.frame(c, x, y, CELL, CELL, border);
        if (item != null) {
            c.item(item, x + 1, y + 1);
            if (Ui.hit(mx, my, x, y, CELL, CELL)) {
                hoverItem = item;
            }
        }
    }

    private int attributes(Canvas c, int x, int y, int w) {
        int labelW = 0;
        for (Attr a : Attr.ALL) {
            labelW = Math.max(labelW, c.textWidth(c.tr(a.langKey())));
        }
        for (Attr a : Attr.ALL) {
            Ui.diamond(c, x + 3, y + 3, 2, Theme.opaque(a.color));
            c.text(c.tr(a.langKey()), x + 10, y, Theme.TEXT, false);
            int ny = Ui.paragraph(c, c.tr(attrKey(a)), x + 18 + labelW, y, w - 18 - labelW, Theme.TEXT_DIM);
            y = Math.max(ny, y + 10) + 3;
        }
        return y;
    }

    private int whetstones(Canvas c, int x, int y, int w, int mx, int my) {
        Kit.sectionLabel(c, c.tr(Lang.CX_WHETSTONES), x, y, w);
        y += 15;
        int colW = w / 2;
        int i = 0;
        for (String affinity : Recipes.WHETSTONE_REAGENTS.keySet()) {
            String id = Rb.id(affinity + "_whetstone");
            Recipes.Recipe r = Recipes.forResult(id);
            if (r == null) {
                continue;
            }
            int cx = x + (i % 2) * colW;
            int cy = y + (i / 2) * (CELL + 3);
            cell(c, cx, cy, id, mx, my, Theme.GOLD_DIM);
            int ix = cx + CELL + 4;
            c.text("=", ix, cy + 5, Theme.TEXT_FAINT, false);
            ix += 9;
            for (String item : r.ingredients()) {
                if (item.equals(Rb.id("blank_whetstone"))) {
                    continue;
                }
                cell(c, ix, cy, item, mx, my, Theme.BORDER_DIM);
                ix += CELL + 1;
            }
            i++;
        }
        return y + ((i + 1) / 2) * (CELL + 3) + 4;
    }

    private int sovereigns(Canvas c, int x, int y, int w, int mx, int my) {
        PlayerData d = ClientState.data();
        for (BossSpec boss : Bosses.all()) {
            if (!boss.summonable()) {
                continue;
            }
            int color = Theme.opaque(boss.color());
            c.fill(x, y, x + 2, y + 44, color);
            c.text(c.tr(boss.nameKey()), x + 8, y, color, false);
            if (!boss.title().isEmpty()) {
                c.text(c.tr(boss.titleKey()), x + 8, y + 10, Theme.TEXT_FAINT, false);
            }
            // Sigil recipe: six fragments plus the token, in one compact row.
            int rx = x + 8;
            int ry = y + 21;
            cell(c, rx, ry, Rb.id("sovereign_fragment"), mx, my, Theme.BORDER_DIM);
            c.text("×6", rx + CELL + 3, ry + 5, Theme.TEXT_DIM, false);
            rx += CELL + 18;
            c.text("+", rx, ry + 5, Theme.TEXT_FAINT, false);
            cell(c, rx + 9, ry, boss.sigilRecipe(), mx, my, Theme.BORDER_DIM);
            c.text("→", rx + 9 + CELL + 5, ry + 5, Theme.GOLD_DIM, false);
            cell(c, rx + 9 + CELL + 18, ry, Rb.id(boss.sigilId()), mx, my, Theme.GOLD_DIM);
            int tx = rx + 9 + CELL * 2 + 26;
            boolean felled = d != null && d.hasFelled(boss.id());
            String blessing = boss.shard() == null ? "" : felled ? c.tr(boss.shard().descKey()) : c.tr(Lang.CODEX_UNKNOWN);
            List<String> lines = new ArrayList<>(Ui.wrap(c, blessing, x + w - tx));
            for (int l = 0; l < Math.min(2, lines.size()); l++) {
                c.text(lines.get(l), tx, ry + (lines.size() == 1 ? 5 : l * 10), felled ? Theme.GOOD : Theme.TEXT_FAINT, false);
            }
            y += 50;
        }
        return y;
    }

    private int controls(Canvas c, int x, int y, int w) {
        y = control(c, x, y, w, "R", c.tr(Lang.CX_KEY_ART));
        y = control(c, x, y, w, "Left Alt", c.tr(Lang.CX_KEY_DODGE));
        y = control(c, x, y, w, "K", c.tr(Lang.CX_KEY_STATUS));
        y = control(c, x, y, w, c.tr(Lang.CX_KEY_FLASK_HOW), c.tr(Lang.CX_KEY_FLASK));
        c.text("§o" + c.tr(Lang.CODEX_DEFAULT_KEY), x, y + 2, Theme.TEXT_FAINT, false);
        return y + 14;
    }

    private int control(Canvas c, int x, int y, int w, String key, String action) {
        int kw = Math.max(18, c.textWidth(key) + 10);
        c.gradient(x, y, x + kw, y + 14, 0xF0272118, 0xF0141110);
        Ui.frame(c, x, y, kw, 14, Theme.BORDER);
        Ui.centered(c, key, x + kw / 2, y + 3, Theme.GOLD_BRIGHT, false);
        c.text(action, x + Math.max(kw, 64) + 8, y + 3, Theme.TEXT, false);
        return y + 18;
    }

    private static String attrKey(Attr a) {
        return switch (a) {
            case VIGOR -> Lang.CX_ATTR_VIG;
            case MIND -> Lang.CX_ATTR_MND;
            case ENDURANCE -> Lang.CX_ATTR_END;
            case STRENGTH -> Lang.CX_ATTR_STR;
            case DEXTERITY -> Lang.CX_ATTR_DEX;
            case INTELLIGENCE -> Lang.CX_ATTR_INT;
            case FAITH -> Lang.CX_ATTR_FTH;
            case ARCANE -> Lang.CX_ATTR_ARC;
        };
    }

    /** Display name of an item id, trying the item key first and then the block key. */
    private static String itemName(Canvas c, String id) {
        int colon = id.indexOf(':');
        String suffix = id.substring(0, colon) + "." + id.substring(colon + 1);
        String name = c.tr("item." + suffix);
        return name.equals("item." + suffix) ? c.tr("block." + suffix) : name;
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return clickHotspot(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        scroll -= (float) amount * 24;
        return true;
    }

    @Override
    public boolean keyPressed(int key) {
        if (key == KEY_ESCAPE && back != null) {
            host.open(back);
            return true;
        }
        return false;
    }

    @Override
    public String titleKey() {
        return Lang.CODEX;
    }
}
