package io.nightbeam.runeborn.core.client.screen;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.RbScreen;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.stat.Leveling;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;

/** Read-only status sheet, opened with the status key. */
public final class CharacterScreen extends RbScreen {
    private int left;
    private int top;
    private int colW;
    private int panelH;

    @Override
    protected void init() {
        colW = Math.max(130, Math.min(150, (width - 40) / 3));
        left = width / 2 - (colW * 3 + 16) / 2;
        panelH = Math.min(height - 60, 214);
        top = Math.max(40, height / 2 - panelH / 2 + 12);
    }

    @Override
    public void render(Canvas c, int mx, int my, float partial) {
        clearHotspots();
        PlayerData d = ClientState.data();
        if (d == null) {
            return;
        }
        Kit.backdrop(c);
        int total = colW * 3 + 16;
        c.title(c.tr(Lang.CHARACTER).toUpperCase(), left, top - 28, Theme.GOLD_BRIGHT, 1.6f);
        int codexW = Math.max(64, c.textWidth(c.tr(Lang.OPEN_CODEX)) + 20);
        int codexX = left + total - codexW;
        boolean codexHover = Ui.hit(mx, my, codexX, top - 30, codexW, 16);
        Ui.button(c, codexX, top - 30, codexW, 16, "§6✦ §r" + c.tr(Lang.OPEN_CODEX), codexHover, true);
        hot(codexX, top - 30, codexW, 16, () -> host.open(new CodexScreen(this)));
        Ui.rule(c, left, top - 10, total, Theme.alpha(Theme.GOLD, 0.7f));
        String hint = ClientState.view.levelingEnabled ? Lang.CHARACTER_HINT : Lang.CHARACTER_HINT_LOCKED;
        Ui.centered(c, c.tr(hint), left + total / 2, top + panelH + 6, Theme.TEXT_FAINT, false);

        // Column 1: identity and attributes
        int x = left;
        Ui.panel(c, x, top, colW, panelH);
        int ix = x + 8;
        int iw = colW - 16;
        int y = top + 8;
        Origin origin = d.originEnum();
        Ui.statRow(c, ix, y, iw, c.tr(Lang.ORIGIN), origin == null ? c.tr(Lang.NONE) : c.tr(origin.nameKey()),
                Theme.TEXT_DIM, Theme.GOLD);
        Ui.statRow(c, ix, y + 11, iw, c.tr(Lang.LEVEL), String.valueOf(d.level()), Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, ix, y + 22, iw, c.tr(Lang.RUNES_HELD), Runes.format(d.runes), Theme.TEXT_DIM, Theme.GOLD);
        Ui.statRow(c, ix, y + 33, iw, c.tr(Lang.RUNES_NEEDED),
                Runes.format(Leveling.cost(d.level(), ClientState.view.levelCostMultiplier)), Theme.TEXT_DIM, Theme.TEXT);
        Kit.sectionLabel(c, c.tr(Lang.ATTRIBUTES), ix, y + 50, iw);
        Kit.attributes(c, ix, y + 64, iw, d.attrs, d.effectiveAttrs(), false, mx, my);

        // Column 2: derived stats and attack power
        x += colW + 8;
        Ui.panel(c, x, top, colW, panelH);
        ix = x + 8;
        Kit.sectionLabel(c, c.tr(Lang.BASE_STATS), ix, top + 6, iw);
        int sy = Kit.derived(c, ix, top + 20, iw, null, d.effectiveAttrs());
        Kit.sectionLabel(c, c.tr(Lang.ATTACK_POWER), ix, sy + 4, iw);
        c.slot(-1, ix, sy + 17);
        int ay = Kit.attackPower(c, ix + 20, sy + 17, iw - 20, host.slot(-1), null, d.effectiveAttrs());
        if (Ui.hit(mx, my, ix, sy + 17, 16, 16)) {
            c.slotTooltip(-1, mx, my);
        }
        if (d.lost != null) {
            c.text("§a◆ §7" + c.tr(Lang.LOST_RUNES), ix, Math.max(ay, sy + 36) + 4, Theme.TEXT_DIM, false);
            c.text(Runes.format(d.lost.amount), ix + 8, Math.max(ay, sy + 36) + 14, 0xFF8FD1A0, false);
        }

        // Column 3: deeds
        x += colW + 8;
        Ui.panel(c, x, top, colW, panelH);
        ix = x + 8;
        Ui.statRow(c, ix, top + 8, iw, c.tr(Lang.KILLS), Runes.format(d.kills), Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, ix, top + 19, iw, c.tr(Lang.CHAMPIONS), Runes.format(d.champions), Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, ix, top + 30, iw, c.tr(Lang.DEATHS), Runes.format(d.deaths), Theme.TEXT_DIM, Theme.TEXT);
        MaterialSpec shard = d.equippedShard.isEmpty() ? null : Materials.get(d.equippedShard);
        Kit.sectionLabel(c, c.tr(Lang.EQUIPPED_SHARD), ix, top + 46, iw);
        if (shard != null) {
            c.item(Rb.id(shard.id()), ix, top + 59);
            int ly = top + 60;
            for (String line : Ui.wrap(c, c.tr(shard.langKey() + ".lore0"), iw - 20)) {
                c.text(line, ix + 20, ly, Theme.TEXT_DIM, false);
                ly += 10;
            }
        } else {
            c.text(c.tr(Lang.NONE), ix, top + 60, Theme.TEXT_FAINT, false);
        }
        Kit.sectionLabel(c, c.tr(Lang.FELLED), ix, top + 86, iw);
        int fy = top + 100;
        for (BossSpec boss : Bosses.all()) {
            if (fy > top + panelH - 10) {
                break;
            }
            boolean felled = d.hasFelled(boss.id());
            Ui.diamond(c, ix + 3, fy + 3, 2, felled ? Theme.opaque(boss.color()) : 0xFF3A352C);
            String name = felled ? c.tr(boss.nameKey()) : "§8???";
            c.text(Ui.wrap(c, name, iw - 10).get(0), ix + 10, fy, felled ? Theme.TEXT : Theme.TEXT_FAINT, false);
            fy += 10;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return clickHotspot(mx, my, button);
    }

    @Override
    public String titleKey() {
        return Lang.CHARACTER;
    }
}
