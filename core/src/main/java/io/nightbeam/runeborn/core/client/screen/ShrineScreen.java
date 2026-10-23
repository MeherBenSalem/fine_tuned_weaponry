package io.nightbeam.runeborn.core.client.screen;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.client.ClientState;
import io.nightbeam.runeborn.core.client.ItemView;
import io.nightbeam.runeborn.core.client.RbScreen;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.net.Packets.ShrineAction;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.shop.Merchant;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Leveling;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.Upgrades;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.util.ArrayList;
import java.util.List;

/** The shrine menu: level up, rest, travel, smithing, flasks, merchant, relics, shards, rebirth. */
public final class ShrineScreen extends RbScreen {
    enum Tab {
        LEVEL_UP(Lang.SHRINE_LEVEL_UP), REST(Lang.SHRINE_REST), TRAVEL(Lang.SHRINE_TRAVEL),
        SMITHING(Lang.SHRINE_SMITHING), INFUSE(Lang.SHRINE_INFUSE), FLASKS(Lang.SHRINE_FLASKS),
        MERCHANT(Lang.SHRINE_MERCHANT), RELICS(Lang.SHRINE_RELICS), SHARDS(Lang.SHRINE_SHARDS),
        REBIRTH(Lang.SHRINE_REBIRTH), LEAVE(Lang.SHRINE_LEAVE);

        final String key;

        Tab(String key) {
            this.key = key;
        }
    }

    private final Packets.Open shrine;
    private Tab tab = Tab.LEVEL_UP;
    private int[] pending;
    private int[] rebirth;
    private int travelSelected = -1;
    private int travelScroll;
    private int smithSlot;
    private Affinity infuseSelected = Affinity.HEAVY;
    private int flaskShare = -1;
    private Merchant.Category category = Merchant.Category.MATERIALS;
    private int merchantScroll;
    private int scroll;

    private int left;
    private int top;
    private int menuW;
    private int contentX;
    private int contentW;
    private int panelH;

    public ShrineScreen(Packets.Open shrine) {
        this.shrine = shrine;
        if (ClientState.view != null && !ClientState.view.levelingEnabled) {
            tab = Tab.REST;
        }
    }

    @Override
    protected void init() {
        menuW = 112;
        contentW = Math.max(220, Math.min(310, width - menuW - 36));
        int total = menuW + 8 + contentW;
        left = width / 2 - total / 2;
        contentX = left + menuW + 8;
        panelH = Math.min(height - 62, 214);
        top = Math.max(40, height / 2 - panelH / 2 + 14);
        smithSlot = host.selectedSlot();
        resetPending();
    }

    private PlayerData data() {
        return ClientState.data();
    }

    private void resetPending() {
        PlayerData d = data();
        pending = d == null ? PlayerData.defaultAttrs() : d.attrs.clone();
        Origin o = d == null ? null : d.originEnum();
        rebirth = o == null ? PlayerData.defaultAttrs() : o.attrs();
        flaskShare = d == null ? 0 : d.flaskEmberShare;
    }

    @Override
    public void render(Canvas c, int mx, int my, float partial) {
        clearHotspots();
        PlayerData d = data();
        if (d == null) {
            return;
        }
        Kit.backdrop(c);

        // Header: shrine name and runes held
        c.title(shrine.shrineName().toUpperCase(), left, top - 30, Theme.GOLD_BRIGHT, 1.6f);
        Ui.rule(c, left, top - 12, menuW + 8 + contentW, Theme.alpha(Theme.GOLD, 0.7f));
        String runes = Runes.format(d.runes);
        int rx = contentX + contentW;
        Ui.right(c, runes, rx, top - 26, Theme.GOLD_BRIGHT, true);
        Ui.runeGlyph(c, rx - c.textWidth(runes) - 8, top - 22);

        // Menu
        Ui.panel(c, left, top, menuW, panelH);
        int rowH = Math.min(18, (panelH - 8) / Tab.values().length);
        int y = top + 4;
        for (Tab t : Tab.values()) {
            boolean hover = Ui.hit(mx, my, left + 2, y, menuW - 4, rowH);
            if (t == tab) {
                Ui.band(c, left + 2, y, menuW - 4, rowH, Theme.ROW_SELECTED);
            } else if (hover) {
                Ui.band(c, left + 2, y, menuW - 4, rowH, Theme.ROW_HOVER);
            }
            boolean enabled = tabEnabled(t, d);
            int color = t == tab ? Theme.GOLD_BRIGHT : enabled ? Theme.TEXT : Theme.TEXT_FAINT;
            Ui.diamond(c, left + 9, y + rowH / 2, 2, t == tab ? Theme.GOLD : Theme.GOLD_DIM);
            c.text(c.tr(t.key), left + 16, y + (rowH - 8) / 2, color, false);
            final Tab target = t;
            hot(left + 2, y, menuW - 4, rowH, () -> select(target));
            y += rowH;
        }

        Ui.panel(c, contentX, top, contentW, panelH);
        int x = contentX + 10;
        int w = contentW - 20;
        int cy = top + 8;
        Kit.header(c, c.tr(tab.key), x, cy, w);
        cy += 24;
        switch (tab) {
            case LEVEL_UP -> levelUp(c, d, x, cy, w, mx, my);
            case REST -> rest(c, d, x, cy, w, mx, my);
            case TRAVEL -> travel(c, d, x, cy, w, mx, my);
            case SMITHING -> smithing(c, d, x, cy, w, mx, my);
            case INFUSE -> infuse(c, d, x, cy, w, mx, my);
            case FLASKS -> flasks(c, d, x, cy, w, mx, my);
            case MERCHANT -> merchant(c, d, x, cy, w, mx, my);
            case RELICS -> relics(c, d, x, cy, w, mx, my);
            case SHARDS -> shards(c, d, x, cy, w, mx, my);
            case REBIRTH -> rebirth(c, d, x, cy, w, mx, my);
            case LEAVE -> {
            }
        }
    }

    private boolean tabEnabled(Tab t, PlayerData d) {
        return switch (t) {
            case LEVEL_UP -> ClientState.view.levelingEnabled;
            case TRAVEL -> ClientState.view.fastTravel && d.shrines.size() > 1;
            case SHARDS -> !d.shards.isEmpty();
            case REBIRTH -> host.count("rebirth_tear") > 0;
            default -> true;
        };
    }

    private void select(Tab t) {
        if (t == Tab.LEAVE) {
            host.close();
            return;
        }
        tab = t;
        scroll = 0;
        resetPending();
    }

    private int bottom() {
        return top + panelH - 22;
    }

    // ------------------------------------------------------------------ Level up

    private void levelUp(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        if (!ClientState.view.levelingEnabled) {
            for (String para : c.tr(Lang.LEVEL_LOCKED).split("\n")) {
                y = Ui.paragraph(c, para, x, y, w, Theme.TEXT_DIM) + 5;
            }
            return;
        }
        int colW = (w - 14) / 2;
        int level = d.level();
        int newLevel = Leveling.level(pending);
        long cost = Leveling.costRange(level, newLevel - level, ClientState.view.levelCostMultiplier);
        long nextCost = Leveling.cost(newLevel, ClientState.view.levelCostMultiplier);

        Ui.statRow(c, x, y, colW, c.tr(Lang.LEVEL), newLevel > level ? level + " §8→ §9" + newLevel : String.valueOf(level),
                Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, x, y + 11, colW, c.tr(Lang.RUNES_HELD), Runes.format(d.runes), Theme.TEXT_DIM, Theme.GOLD);
        long shownCost = newLevel > level ? cost : nextCost;
        boolean affordable = d.runes >= shownCost;
        Ui.statRow(c, x, y + 22, colW, c.tr(Lang.RUNES_NEEDED), Runes.format(shownCost), Theme.TEXT_DIM,
                affordable ? Theme.TEXT : Theme.BAD);

        int ay = y + 40;
        Kit.sectionLabel(c, c.tr(Lang.ATTRIBUTES), x, ay - 3, colW);
        ay += 12;
        Kit.attributes(c, x, ay, colW, d.attrs, pending, true, mx, my);
        for (Attr a : Attr.ALL) {
            int i = a.ordinal();
            int ry = ay + i * 13 - 2;
            hot(x + colW - 24, ry, 11, 11, () -> {
                if (pending[i] > d.attrs[i]) {
                    pending[i]--;
                }
            });
            hot(x + colW - 11, ry, 11, 11, () -> {
                int lvl = Leveling.level(pending);
                long total = Leveling.costRange(d.level(), lvl + 1 - d.level(), ClientState.view.levelCostMultiplier);
                if (pending[i] < Attr.MAX && (total <= d.runes || host.creative())) {
                    pending[i]++;
                }
            });
        }

        int sx = x + colW + 14;
        Kit.sectionLabel(c, c.tr(Lang.BASE_STATS), sx, y - 3, colW);
        int sy = Kit.derived(c, sx, y + 10, colW, d.effectiveAttrs(), withShard(d, pending));
        Kit.sectionLabel(c, c.tr(Lang.ATTACK_POWER), sx, sy + 4, colW);
        Kit.attackPower(c, sx, sy + 17, colW, host.slot(-1), d.effectiveAttrs(), withShard(d, pending));

        boolean changed = newLevel > level;
        int bw = 70;
        int bx = x + w - bw;
        int by = bottom();
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.CONFIRM), Ui.hit(mx, my, bx, by, bw, 15), changed && affordable);
        if (changed && affordable) {
            hot(bx, by, bw, 15, () -> host.send(Packets.levelUp(pending.clone())));
        }
        Ui.button(c, bx - bw - 6, by, bw, 15, c.tr(Lang.RESET), Ui.hit(mx, my, bx - bw - 6, by, bw, 15), changed);
        hot(bx - bw - 6, by, bw, 15, this::resetPending);
        int rpg = ClientState.view.rpgLevels;
        if (rpg > 0) {
            String note = changed ? "§6✦ §r" + c.tr(Lang.LEVEL_RPG_BONUS, (newLevel - level) * rpg)
                    : c.tr(Lang.LEVEL_RPG_NOTE, rpg);
            // Left of the Reset button; skipped when a translation is too long to fit.
            if (c.textWidth(note) <= bx - bw - 6 - x - 6) {
                c.text(note, x, by + 4, changed ? Theme.GOLD : Theme.TEXT_FAINT, false);
            }
        }
    }

    /** Applies the equipped crown shard's attribute bonus to a pending allocation. */
    private static int[] withShard(PlayerData d, int[] attrs) {
        int[] base = d.attrs;
        int[] eff = d.effectiveAttrs();
        int[] out = new int[attrs.length];
        for (int i = 0; i < attrs.length; i++) {
            out[i] = Math.min(Attr.MAX, attrs[i] + (eff[i] - base[i]));
        }
        return out;
    }

    // ------------------------------------------------------------------ Rest

    private void rest(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        y = Ui.paragraph(c, c.tr(Lang.REST_DESC), x, y, w, Theme.TEXT_DIM);
        boolean bound = d.rested != null
                && d.rested.at(shrine.dimension(), shrine.x(), shrine.y(), shrine.z());
        if (bound) {
            c.text("§6◆ §r" + c.tr(Lang.REST_BOUND), x, y + 6, Theme.GOLD, false);
        }
        int fy = y + 24;
        Ui.softPanel(c, x, fy, w, 26);
        c.item(Rb.id("ember_flask"), x + 4, fy + 5);
        c.text(d.emberLeft + " / " + d.flaskEmberShare, x + 24, fy + 9, Theme.TEXT, false);
        c.item(Rb.id("azure_flask"), x + 70, fy + 5);
        c.text(d.azureLeft + " / " + d.azureShare(), x + 90, fy + 9, Theme.TEXT, false);
        int bw = 110;
        int bx = x + w / 2 - bw / 2;
        int by = bottom();
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.REST_BUTTON), Ui.hit(mx, my, bx, by, bw, 15), true);
        hot(bx, by, bw, 15, () -> host.send(Packets.shrine(ShrineAction.REST, 0, 0, "")));
    }

    // ------------------------------------------------------------------ Travel

    private void travel(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        if (!ClientState.view.fastTravel) {
            c.text(c.tr(Lang.TRAVEL_DISABLED), x, y, Theme.TEXT_DIM, false);
            return;
        }
        List<ShrineRecord> list = d.shrines;
        if (list.size() <= 1) {
            Ui.paragraph(c, c.tr(Lang.TRAVEL_EMPTY), x, y, w, Theme.TEXT_DIM);
            return;
        }
        int rowH = 20;
        int visible = Math.max(1, (bottom() - 6 - y) / rowH);
        travelScroll = Math.max(0, Math.min(travelScroll, list.size() - visible));
        int ry = y;
        for (int i = travelScroll; i < Math.min(list.size(), travelScroll + visible); i++) {
            ShrineRecord s = list.get(i);
            boolean here = s.at(shrine.dimension(), shrine.x(), shrine.y(), shrine.z());
            boolean hover = Ui.hit(mx, my, x, ry, w, rowH - 2);
            if (i == travelSelected) {
                Ui.band(c, x, ry, w, rowH - 2, Theme.ROW_SELECTED);
            } else if (hover) {
                Ui.band(c, x, ry, w, rowH - 2, Theme.ROW_HOVER);
            }
            Ui.diamond(c, x + 5, ry + 8, 2, here ? Theme.GOLD_BRIGHT : Theme.GOLD_DIM);
            c.text(s.name, x + 12, ry + 1, here ? Theme.GOLD_BRIGHT : Theme.TEXT, false);
            String where = dimensionName(s.dimension) + "  " + s.x + ", " + s.y + ", " + s.z;
            c.text(where, x + 12, ry + 10, Theme.TEXT_FAINT, false);
            if (here) {
                Ui.right(c, c.tr(Lang.TRAVEL_HERE), x + w - 2, ry + 1, Theme.TEXT_DIM, false);
            } else if (s.dimension.equals(host.dimension())) {
                double dx = s.x - host.x();
                double dz = s.z - host.z();
                Ui.right(c, Math.round(Math.sqrt(dx * dx + dz * dz)) + "m", x + w - 2, ry + 1, Theme.TEXT_DIM, false);
            }
            final int index = i;
            if (!here) {
                hot(x, ry, w, rowH - 2, () -> travelSelected = index);
            }
            ry += rowH;
        }
        int bw = 80;
        int bx = x + w - bw;
        int by = bottom();
        boolean can = travelSelected >= 0 && travelSelected < list.size();
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.SHRINE_TRAVEL), Ui.hit(mx, my, bx, by, bw, 15), can);
        if (can) {
            final int index = travelSelected;
            hot(bx, by, bw, 15, () -> {
                host.send(Packets.shrine(ShrineAction.TRAVEL, index, 0, ""));
                host.close();
            });
        }
    }

    private static String dimensionName(String dim) {
        return switch (dim) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "Nether";
            case "minecraft:the_end" -> "The End";
            default -> dim.substring(dim.indexOf(':') + 1).replace('_', ' ');
        };
    }

    // ------------------------------------------------------------------ Smithing and infusion

    /** Hotbar strip used to pick the armament. Returns the y below it. */
    private int hotbar(Canvas c, int x, int y, int w, int mx, int my) {
        c.text(c.tr(Lang.SMITH_SELECT), x, y, Theme.TEXT_DIM, false);
        y += 12;
        int size = 20;
        int gap = Math.max(1, (w - size * 9) / 8);
        for (int i = 0; i < 9; i++) {
            int sx = x + i * (size + gap);
            boolean sel = i == smithSlot;
            Ui.softPanel(c, sx, y, size, size);
            if (sel) {
                Ui.frame(c, sx, y, size, size, Theme.GOLD);
            }
            c.slot(i, sx + 2, y + 2);
            final int slot = i;
            hot(sx, y, size, size, () -> smithSlot = slot);
            if (Ui.hit(mx, my, sx, y, size, size)) {
                c.slotTooltip(i, mx, my);
            }
        }
        return y + size + 8;
    }

    private void smithing(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        y = hotbar(c, x, y, w, mx, my);
        ItemView item = host.slot(smithSlot);
        WeaponSpec spec = item.weapon();
        if (spec == null) {
            c.text(c.tr(Lang.SMITH_NOT_WEAPON), x, y, Theme.TEXT_FAINT, false);
            return;
        }
        int cur = item.upgrade();
        String name = c.tr(spec.langKey());
        c.text(spec.rarity.code + name, x, y, Theme.TEXT, false);
        Upgrades.Cost cost = Upgrades.next(spec, cur, ClientState.view.smithingCostMultiplier);
        Ui.right(c, cost == null ? "+" + cur : "+" + cur + " §8→ §9+" + (cur + 1), x + w, y, Theme.TEXT, false);
        y += 14;
        int colW = (w - 14) / 2;
        int[] attrs = d.effectiveAttrs();
        Kit.sectionLabel(c, c.tr(Lang.ATTACK_POWER), x, y, colW);
        ItemView next = cost == null ? item
                : new ItemView(item.slot(), item.itemId(), item.weaponId(), cur + 1, item.affinity(), item.count(), item.name());
        Kit.attackPowerCompare(c, x, y + 13, colW, item, next, attrs);

        int sx = x + colW + 14;
        if (cost == null) {
            c.text(c.tr(Lang.SMITH_MAXED), sx, y + 13, Theme.GOLD, false);
            return;
        }
        Kit.sectionLabel(c, c.tr(Lang.SMITH_NEEDS), sx, y, colW);
        int have = host.count(cost.materialId());
        Ui.softPanel(c, sx, y + 14, 20, 20);
        c.item(Rb.id(cost.materialId()), sx + 2, y + 16);
        if (Ui.hit(mx, my, sx, y + 14, 20, 20)) {
            c.itemTooltip(Rb.id(cost.materialId()), mx, my);
        }
        MaterialSpec mat = Materials.get(cost.materialId());
        String matName = mat == null ? cost.materialId() : c.tr(mat.langKey());
        c.text(Ui.wrap(c, matName, colW - 26).get(0), sx + 24, y + 15, Theme.TEXT, false);
        c.text(have + " / " + cost.count(), sx + 24, y + 25, have >= cost.count() ? Theme.GOOD : Theme.BAD, false);
        boolean runesOk = d.runes >= cost.runes() || host.creative();
        Ui.runeGlyph(c, sx + 4, y + 45);
        c.text(Runes.format(cost.runes()), sx + 12, y + 41, runesOk ? Theme.GOLD : Theme.BAD, false);

        boolean can = (have >= cost.count() || host.creative()) && runesOk;
        int bw = 80;
        int bx = x + w - bw;
        int by = bottom();
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.SMITH_BUTTON), Ui.hit(mx, my, bx, by, bw, 15), can);
        if (can) {
            final int slot = smithSlot;
            hot(bx, by, bw, 15, () -> host.send(Packets.shrine(ShrineAction.UPGRADE, slot, 0, "")));
        }
    }

    private void infuse(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        y = hotbar(c, x, y, w, mx, my);
        ItemView item = host.slot(smithSlot);
        WeaponSpec spec = item.weapon();
        if (spec == null) {
            c.text(c.tr(Lang.SMITH_NOT_WEAPON), x, y, Theme.TEXT_FAINT, false);
            return;
        }
        if (!spec.infusable()) {
            c.text(c.tr(Lang.INFUSE_LOCKED), x, y, Theme.TEXT_FAINT, false);
            return;
        }
        int cols = 4;
        int bw = (w - (cols - 1) * 3) / cols;
        int bh = 13;
        int i = 0;
        for (Affinity a : Affinity.ALL) {
            int col = i % cols;
            int row = i / cols;
            int bx = x + col * (bw + 3);
            int by = y + row * (bh + 2);
            boolean owned = a == Affinity.STANDARD || host.count(a.id() + "_whetstone") > 0 || host.creative();
            boolean sel = a == infuseSelected;
            boolean current = a == item.affinity();
            int top = sel ? 0xF0352C1E : 0xF0181512;
            c.gradient(bx, by, bx + bw, by + bh, top, 0xF0100E0C);
            Ui.frame(c, bx, by, bw, bh, sel ? Theme.GOLD : current ? Theme.GOLD_DIM : Theme.BORDER_DIM);
            c.fill(bx + 2, by + 2, bx + 4, by + bh - 2, Theme.opaque(a.color));
            String label = c.tr(a.langKey());
            int color = owned ? (current ? Theme.GOLD : Theme.TEXT) : Theme.TEXT_FAINT;
            c.text(label, bx + 7, by + 3, color, false);
            final Affinity choice = a;
            hot(bx, by, bw, bh, () -> infuseSelected = choice);
            i++;
        }
        int rows = (Affinity.ALL.length + cols - 1) / cols;
        y += rows * (bh + 2) + 6;
        int colW = (w - 14) / 2;
        ItemView next = new ItemView(item.slot(), item.itemId(), item.weaponId(), item.upgrade(), infuseSelected,
                item.count(), item.name());
        Kit.sectionLabel(c, c.tr(Lang.ATTACK_POWER), x, y, colW);
        Kit.attackPowerCompare(c, x, y + 13, colW, item, next, d.effectiveAttrs());
        int sx = x + colW + 14;
        Kit.scalingCompare(c, sx, y, colW, bottom() - 2, item, next, d.effectiveAttrs());

        boolean owned = infuseSelected == Affinity.STANDARD || host.count(infuseSelected.id() + "_whetstone") > 0
                || host.creative();
        long cost = Upgrades.infusionCost(ClientState.view.smithingCostMultiplier);
        boolean can = owned && infuseSelected != item.affinity() && (d.runes >= cost || host.creative());
        int bw2 = 80;
        int bx = x + w - bw2;
        int by = bottom();
        if (!owned) {
            c.text(c.tr(Lang.INFUSE_NEEDS), x, by + 4, Theme.BAD, false);
        } else {
            Ui.runeGlyph(c, x + 4, by + 7);
            c.text(Runes.format(cost), x + 12, by + 4, d.runes >= cost ? Theme.GOLD : Theme.BAD, false);
        }
        Ui.button(c, bx, by, bw2, 15, c.tr(Lang.INFUSE_BUTTON), Ui.hit(mx, my, bx, by, bw2, 15), can);
        if (can) {
            final int slot = smithSlot;
            final Affinity a = infuseSelected;
            hot(bx, by, bw2, 15, () -> host.send(Packets.shrine(ShrineAction.INFUSE, slot, a.ordinal(), "")));
        }
    }

    // ------------------------------------------------------------------ Flasks

    private void flasks(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        y = Ui.paragraph(c, c.tr(Lang.FLASK_DESC), x, y, w, Theme.TEXT_DIM) + 6;
        if (flaskShare < 0 || flaskShare > d.flaskCharges) {
            flaskShare = d.flaskEmberShare;
        }
        int half = (w - 10) / 2;
        flaskCard(c, x, y, half, "ember_flask", flaskShare, mx, my, -1);
        flaskCard(c, x + half + 10, y, half, "azure_flask", d.flaskCharges - flaskShare, mx, my, 1);
        y += 58;
        Ui.statRow(c, x, y, w, c.tr(Lang.FLASK_CHARGES), d.flaskCharges + " / " + PlayerData.MAX_FLASK_CHARGES,
                Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, x, y + 11, w, c.tr(Lang.FLASK_POTENCY), "+" + d.flaskPotency + " / +" + PlayerData.MAX_FLASK_POTENCY,
                Theme.TEXT_DIM, Theme.TEXT);
        Ui.statRow(c, x, y + 22, w, c.tr(Lang.HEALTH), Math.round(d.emberHealFraction() * 100) + "%", Theme.TEXT_DIM,
                Theme.HP_LIGHT);
        Ui.statRow(c, x, y + 33, w, c.tr(Lang.FOCUS), String.valueOf(Math.round(d.azureRestore())), Theme.TEXT_DIM,
                Theme.BLUE);
        boolean changed = flaskShare != d.flaskEmberShare;
        int bw = 70;
        int bx = x + w - bw;
        int by = bottom();
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.CONFIRM), Ui.hit(mx, my, bx, by, bw, 15), changed);
        if (changed) {
            hot(bx, by, bw, 15, () -> host.send(Packets.shrine(ShrineAction.FLASK_SPLIT, flaskShare, 0, "")));
        }
    }

    private void flaskCard(Canvas c, int x, int y, int w, String item, int value, int mx, int my, int dir) {
        Ui.softPanel(c, x, y, w, 50);
        c.push();
        c.translate(x + w / 2f - 16, y + 4);
        c.scale(2f);
        c.item(Rb.id(item), 0, 0);
        c.pop();
        Ui.centered(c, String.valueOf(value), x + w / 2, y + 39, Theme.TEXT, true);
        int bx = x + 4;
        int by = y + 37;
        Ui.stepper(c, bx, by, "-", Ui.hit(mx, my, bx, by, 11, 11), value > 0);
        Ui.stepper(c, x + w - 15, by, "+", Ui.hit(mx, my, x + w - 15, by, 11, 11), true);
        PlayerData d = data();
        // dir = -1 for ember (left card), +1 for azure; moving charges between the two.
        hot(bx, by, 11, 11, () -> flaskShare = dir < 0 ? Math.max(0, flaskShare - 1) : Math.min(d.flaskCharges, flaskShare + 1));
        hot(x + w - 15, by, 11, 11, () -> flaskShare = dir < 0 ? Math.min(d.flaskCharges, flaskShare + 1) : Math.max(0, flaskShare - 1));
    }

    // ------------------------------------------------------------------ Merchant

    private void merchant(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        Merchant.Category[] cats = Merchant.Category.values();
        int tw = (w - (cats.length - 1) * 3) / cats.length;
        for (int i = 0; i < cats.length; i++) {
            Merchant.Category cat = cats[i];
            int bx = x + i * (tw + 3);
            boolean sel = cat == category;
            Ui.button(c, bx, y - 4, tw, 13, cap(cat.name()), sel || Ui.hit(mx, my, bx, y - 4, tw, 13), true);
            hot(bx, y - 4, tw, 13, () -> {
                category = cat;
                merchantScroll = 0;
            });
        }
        y += 13;
        List<Integer> rows = new ArrayList<>();
        List<Merchant.Offer> offers = Merchant.offers();
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).category() == category) {
                rows.add(i);
            }
        }
        int rowH = 20;
        int visible = Math.max(1, (bottom() + 14 - y) / rowH);
        merchantScroll = Math.max(0, Math.min(merchantScroll, rows.size() - visible));
        int sovereigns = d.sovereignsFelled();
        for (int r = merchantScroll; r < Math.min(rows.size(), merchantScroll + visible); r++) {
            int index = rows.get(r);
            Merchant.Offer offer = offers.get(index);
            int ry = y + (r - merchantScroll) * rowH;
            boolean locked = offer.sovereignsRequired() > sovereigns && !host.creative();
            long price = offer.price(ClientState.view.merchantPriceMultiplier);
            boolean afford = d.runes >= price || host.creative();
            boolean hover = Ui.hit(mx, my, x, ry, w, rowH - 2);
            if (hover && !locked) {
                Ui.band(c, x, ry, w, rowH - 2, Theme.ROW_HOVER);
            }
            c.item(Rb.id(offer.itemId()), x + 1, ry + 1);
            String name = c.tr("item.runeborn." + offer.itemId());
            WeaponSpec ws = Weapons.get(offer.itemId());
            String prefix = ws != null ? ws.rarity.code : "";
            c.text(prefix + Ui.wrap(c, name, w - 100).get(0), x + 21, ry + 1, locked ? Theme.TEXT_FAINT : Theme.TEXT, false);
            if (locked) {
                c.text(c.tr(Lang.MERCHANT_LOCKED) + " (" + offer.sovereignsRequired() + ")", x + 21, ry + 10,
                        Theme.TEXT_FAINT, false);
            } else if (ws != null) {
                c.text(c.tr(ws.cls.langKey()), x + 21, ry + 10, Theme.TEXT_FAINT, false);
            }
            String priceText = Runes.format(price);
            Ui.right(c, priceText, x + w - 2, ry + 5, locked ? Theme.TEXT_FAINT : afford ? Theme.GOLD : Theme.BAD, false);
            Ui.runeGlyph(c, x + w - 9 - c.textWidth(priceText), ry + 9);
            if (Ui.hit(mx, my, x, ry, 18, 18)) {
                c.itemTooltip(Rb.id(offer.itemId()), mx, my);
            }
            if (!locked) {
                hot(x, ry, w, rowH - 2, button -> host.send(
                        Packets.shrine(ShrineAction.BUY, index, button == 1 ? 5 : 1, "")));
            }
        }
        if (rows.size() > visible) {
            int trackH = visible * rowH - 2;
            int thumb = Math.max(10, trackH * visible / rows.size());
            int ty = y + (trackH - thumb) * merchantScroll / Math.max(1, rows.size() - visible);
            c.fill(x + w + 4, y, x + w + 5, y + trackH, Theme.BORDER_DIM);
            c.fill(x + w + 3, ty, x + w + 6, ty + thumb, Theme.GOLD_DIM);
        }
    }

    private static String cap(String constant) {
        return constant.charAt(0) + constant.substring(1).toLowerCase();
    }

    // ------------------------------------------------------------------ Relics

    private void relics(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        int ry = y;
        boolean any = false;
        for (BossSpec boss : Bosses.all()) {
            if (host.count(boss.relicId()) <= 0) {
                continue;
            }
            any = true;
            if (ry > bottom() - 30) {
                break;
            }
            c.item(Rb.id(boss.relicId()), x, ry);
            c.text(c.tr("item.runeborn." + boss.relicId()), x + 20, ry + 4, Theme.GOLD, false);
            ry += 20;
            int half = (w - 6) / 2;
            for (int i = 0; i < boss.relics().size(); i++) {
                String weaponId = boss.relics().get(i);
                WeaponSpec spec = Weapons.get(weaponId);
                if (spec == null) {
                    continue;
                }
                int bx = x + i * (half + 6);
                boolean hover = Ui.hit(mx, my, bx, ry, half, 22);
                Ui.softPanel(c, bx, ry, half, 22);
                if (hover) {
                    Ui.frame(c, bx, ry, half, 22, Theme.GOLD);
                }
                c.item(Rb.id(weaponId), bx + 3, ry + 3);
                c.text(spec.rarity.code + Ui.wrap(c, c.tr(spec.langKey()), half - 26).get(0), bx + 22, ry + 3, Theme.TEXT, false);
                c.text(c.tr(spec.cls.langKey()), bx + 22, ry + 12, Theme.TEXT_FAINT, false);
                if (hover) {
                    c.itemTooltip(Rb.id(weaponId), mx, my);
                }
                final int choice = i;
                hot(bx, ry, half, 22, () -> host.send(Packets.shrine(ShrineAction.EXCHANGE, choice, 0, boss.id())));
            }
            ry += 30;
        }
        if (!any) {
            Ui.paragraph(c, c.tr(Lang.RELIC_EMPTY), x, y, w, Theme.TEXT_DIM);
        }
    }

    // ------------------------------------------------------------------ Crown shards

    private void shards(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        if (d.shards.isEmpty()) {
            Ui.paragraph(c, c.tr(Lang.SHARD_EMPTY), x, y, w, Theme.TEXT_DIM);
            return;
        }
        int ry = y;
        for (String shardId : d.shards) {
            if (ry > bottom() - 10) {
                break;
            }
            MaterialSpec spec = Materials.get(shardId);
            if (spec == null) {
                continue;
            }
            boolean equipped = shardId.equals(d.equippedShard);
            Ui.softPanel(c, x, ry, w, 30);
            if (equipped) {
                Ui.frame(c, x, ry, w, 30, Theme.GOLD);
            }
            c.item(Rb.id(shardId), x + 4, ry + 7);
            c.text(c.tr(spec.langKey()), x + 24, ry + 4, equipped ? Theme.GOLD_BRIGHT : Theme.TEXT, false);
            c.text(Ui.wrap(c, c.tr(spec.langKey() + ".lore0"), w - 90).get(0), x + 24, ry + 15, Theme.TEXT_DIM, false);
            int bw = 54;
            int bx = x + w - bw - 4;
            Ui.button(c, bx, ry + 8, bw, 14, c.tr(equipped ? Lang.SHARD_EQUIPPED : Lang.SHARD_EQUIP),
                    Ui.hit(mx, my, bx, ry + 8, bw, 14), !equipped);
            if (!equipped) {
                hot(bx, ry + 8, bw, 14, () -> host.send(Packets.shrine(ShrineAction.EQUIP_SHARD, 0, 0, shardId)));
            }
            ry += 34;
        }
    }

    // ------------------------------------------------------------------ Rebirth

    private void rebirth(Canvas c, PlayerData d, int x, int y, int w, int mx, int my) {
        y = Ui.paragraph(c, c.tr(Lang.REBIRTH_DESC), x, y, w, Theme.TEXT_DIM) + 4;
        boolean hasTear = host.count("rebirth_tear") > 0;
        if (!hasTear) {
            c.text(c.tr(Lang.REBIRTH_NEEDS), x, y, Theme.BAD, false);
            return;
        }
        Origin origin = d.originEnum();
        int[] floor = origin == null ? PlayerData.defaultAttrs() : origin.attrs();
        int total = sum(d.attrs);
        int remaining = total - sum(rebirth);
        int colW = (w - 14) / 2;
        Ui.statRow(c, x, y, colW, c.tr(Lang.REBIRTH_POINTS), String.valueOf(remaining), Theme.TEXT_DIM,
                remaining == 0 ? Theme.GOOD : Theme.GOLD);
        int ay = y + 16;
        Kit.attributes(c, x, ay, colW, floor, rebirth, true, mx, my);
        for (Attr a : Attr.ALL) {
            int i = a.ordinal();
            int ry = ay + i * 13 - 2;
            hot(x + colW - 24, ry, 11, 11, () -> {
                if (rebirth[i] > floor[i]) {
                    rebirth[i]--;
                }
            });
            hot(x + colW - 11, ry, 11, 11, () -> {
                if (rebirth[i] < Attr.MAX && sum(rebirth) < sum(d.attrs)) {
                    rebirth[i]++;
                }
            });
        }
        int sx = x + colW + 14;
        Kit.sectionLabel(c, c.tr(Lang.BASE_STATS), sx, y - 3, colW);
        Kit.derived(c, sx, y + 10, colW, d.attrs, rebirth);
        int bw = 70;
        int bx = x + w - bw;
        int by = bottom();
        boolean can = remaining == 0;
        Ui.button(c, bx, by, bw, 15, c.tr(Lang.CONFIRM), Ui.hit(mx, my, bx, by, bw, 15), can);
        if (can) {
            hot(bx, by, bw, 15, () -> host.send(Packets.rebirth(rebirth.clone())));
        }
    }

    private static int sum(int[] a) {
        int s = 0;
        for (int v : a) {
            s += v;
        }
        return s;
    }

    // ------------------------------------------------------------------ Input

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return clickHotspot(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        int step = amount > 0 ? -1 : 1;
        if (tab == Tab.MERCHANT) {
            merchantScroll = Math.max(0, merchantScroll + step);
            return true;
        }
        if (tab == Tab.TRAVEL) {
            travelScroll = Math.max(0, travelScroll + step);
            return true;
        }
        if (tab == Tab.SMITHING || tab == Tab.INFUSE) {
            smithSlot = Math.floorMod(smithSlot + step, 9);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key) {
        Tab[] tabs = Tab.values();
        if (key == 264) {
            select(tabs[(tab.ordinal() + 1) % (tabs.length - 1)]);
            return true;
        }
        if (key == 265) {
            select(tabs[Math.floorMod(tab.ordinal() - 1, tabs.length - 1)]);
            return true;
        }
        return false;
    }

    @Override
    public String titleKey() {
        return Lang.SHRINE;
    }
}
