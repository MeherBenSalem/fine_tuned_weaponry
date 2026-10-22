package io.nightbeam.runeborn.core.client.screen;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.RbScreen;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

/** First-join class selection. */
public final class OriginScreen extends RbScreen {
    private int selected;
    private int left;
    private int top;
    private int listW;
    private int detailX;
    private int detailW;
    private int panelH;

    @Override
    protected void init() {
        listW = 124;
        detailW = Math.min(300, width - listW - 40);
        int total = listW + 10 + detailW;
        left = width / 2 - total / 2;
        detailX = left + listW + 10;
        panelH = Math.min(height - 70, 196);
        top = Math.max(44, height / 2 - panelH / 2 + 10);
    }

    @Override
    public void render(Canvas c, int mx, int my, float partial) {
        Kit.backdrop(c);
        float t = c.time();
        Ui.titleCentered(c, c.tr(Lang.ORIGIN_TITLE).toUpperCase(), width / 2, top - 34, Theme.GOLD, 2.0f);
        Ui.centered(c, c.tr(Lang.ORIGIN_SUBTITLE), width / 2, top - 13, Theme.TEXT_DIM, false);

        // Class list
        Ui.panel(c, left, top, listW, panelH);
        Ui.centered(c, c.tr(Lang.ORIGIN_HINT), width / 2, top + panelH + 6, Theme.TEXT_FAINT, false);
        int rowH = Math.max(14, Math.min(18, (panelH - 12) / Origin.ALL.length));
        int y = top + 6;
        for (int i = 0; i < Origin.ALL.length; i++) {
            Origin o = Origin.ALL[i];
            boolean hover = Ui.hit(mx, my, left + 2, y, listW - 4, rowH);
            if (i == selected) {
                Ui.band(c, left + 2, y, listW - 4, rowH, Theme.ROW_SELECTED);
            } else if (hover) {
                Ui.band(c, left + 2, y, listW - 4, rowH, Theme.ROW_HOVER);
            }
            int ty = y + (rowH - 8) / 2;
            c.text(c.tr(o.nameKey()), left + 9, ty, i == selected ? Theme.GOLD_BRIGHT : Theme.TEXT, false);
            Ui.right(c, String.valueOf(o.level()), left + listW - 8, ty, Theme.TEXT_DIM, false);
            y += rowH;
        }

        // Detail
        Origin o = Origin.ALL[selected];
        Ui.panel(c, detailX, top, detailW, panelH);
        int x = detailX + 10;
        int w = detailW - 20;
        c.title(c.tr(o.nameKey()).toUpperCase(), x, top + 8, Theme.GOLD_BRIGHT, 1.6f);
        Ui.right(c, c.tr(Lang.LEVEL) + " " + o.level(), x + w, top + 11, Theme.TEXT_DIM, false);
        Ui.rule(c, x, top + 26, w, Theme.alpha(Theme.GOLD, 0.6f));
        int dy = Ui.paragraph(c, c.tr(o.descKey()), x, top + 32, w, Theme.TEXT_DIM);

        int colW = (w - 12) / 2;
        int ay = dy + 6;
        int[] attrs = o.attrs();
        for (Attr a : Attr.ALL) {
            int col = a.ordinal() / 4;
            int row = a.ordinal() % 4;
            int ax = x + col * (colW + 12);
            int ry = ay + row * 15;
            c.text(c.tr(a.langKey()), ax, ry, Theme.TEXT, false);
            Ui.right(c, String.valueOf(attrs[a.ordinal()]), ax + colW, ry, Theme.GOLD, false);
            float fill = attrs[a.ordinal()] / 18f;
            c.fill(ax, ry + 10, ax + colW, ry + 11, 0x40FFFFFF);
            c.fill(ax, ry + 10, ax + Math.round(colW * Ui.clamp01(fill)), ry + 11, Theme.opaque(a.color));
        }

        int wy = ay + 4 * 15 + 6;
        WeaponSpec weapon = Weapons.get(o.weapon);
        if (weapon != null && wy + 22 < top + panelH - 22) {
            Kit.sectionLabel(c, c.tr(Lang.ORIGIN_WEAPON), x, wy, w);
            Ui.softPanel(c, x, wy + 14, 20, 20);
            c.item(Rb.id(weapon.id), x + 2, wy + 16);
            c.text(Ui.wrap(c, c.tr(weapon.langKey()), w - 30).get(0), x + 26, wy + 16, Theme.TEXT, false);
            c.text(c.tr(weapon.cls.langKey()), x + 26, wy + 26, Theme.TEXT_FAINT, false);
            if (Ui.hit(mx, my, x, wy + 14, 20, 20)) {
                c.itemTooltip(Rb.id(weapon.id), mx, my);
            }
        }

        int bw = 80;
        int bx = detailX + detailW - bw - 10;
        int by = top + panelH - 22;
        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 0.15f);
        boolean hover = Ui.hit(mx, my, bx, by, bw, 15);
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.ORIGIN_BEGIN).toUpperCase(), hover || pulse > 0.85f, true);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int rowH = Math.max(14, Math.min(18, (panelH - 12) / Origin.ALL.length));
        int y = top + 6;
        for (int i = 0; i < Origin.ALL.length; i++) {
            if (Ui.hit(mx, my, left + 2, y, listW - 4, rowH)) {
                if (selected != i) {
                    selected = i;
                    click();
                }
                return true;
            }
            y += rowH;
        }
        int bw = 80;
        if (Ui.hit(mx, my, detailX + detailW - bw - 10, top + panelH - 22, bw, 15)) {
            click();
            host.send(Packets.chooseOrigin(Origin.ALL[selected].id()));
            host.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        selected = Math.floorMod(selected - (int) Math.signum(amount), Origin.ALL.length);
        return true;
    }

    @Override
    public boolean keyPressed(int key) {
        if (key == 264) { // down
            selected = (selected + 1) % Origin.ALL.length;
            return true;
        }
        if (key == 265) { // up
            selected = Math.floorMod(selected - 1, Origin.ALL.length);
            return true;
        }
        return false;
    }

    @Override
    public boolean closeOnEscape() {
        return ClientState.view == null || !ClientState.view.requireClass || ClientState.data().hasOrigin();
    }

    @Override
    public String titleKey() {
        return Lang.ORIGIN_TITLE;
    }
}
