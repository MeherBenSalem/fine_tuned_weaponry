package io.nightbeam.runeborn.core.client.screen;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.client.Hud;
import io.nightbeam.runeborn.core.client.ItemView;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Theme;
import io.nightbeam.runeborn.core.ui.Ui;
import io.nightbeam.runeborn.core.weapon.AttackRating;
import io.nightbeam.runeborn.core.weapon.DamageType;
import io.nightbeam.runeborn.core.weapon.Grade;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Layout pieces shared by the origin, shrine and status screens. */
final class Kit {
    /** Darkened backdrop with a vignette, like resting at a site of flame. */
    static void backdrop(Canvas c) {
        Hud.hideForMenu();
        int w = c.width();
        int h = c.height();
        c.fill(0, 0, w, h, Theme.SCRIM);
        c.gradient(0, 0, w, h / 3, 0x90000000, 0x00000000);
        c.gradient(0, h * 2 / 3, w, h, 0x00000000, 0xA0000000);
        // Warm ember glow rising from the bottom centre.
        int glow = Math.min(w, 520);
        Ui.hgradient(c, w / 2 - glow / 2, h - 60, w / 2, h, 0x00000000, 0x2A1E0C00);
        Ui.hgradient(c, w / 2, h - 60, w / 2 + glow / 2, h, 0x2A1E0C00, 0x00000000);
    }

    static void header(Canvas c, String title, int x, int y, int w) {
        c.title(title.toUpperCase(Locale.ROOT), x, y, Theme.GOLD, 1.5f);
        Ui.rule(c, x, y + 16, w, Theme.alpha(Theme.GOLD, 0.7f));
    }

    static void sectionLabel(Canvas c, String text, int x, int y, int w) {
        c.text(text, x, y, Theme.GOLD_DIM, false);
        c.fill(x, y + 10, x + w, y + 11, Theme.BORDER_DIM);
    }

    /**
     * Attribute rows. When {@code steppers} is true, draws -/+ buttons and highlights raised
     * values. Returns the y below the block.
     */
    static int attributes(Canvas c, int x, int y, int w, int[] base, int[] shown, boolean steppers, int mx, int my) {
        for (Attr a : Attr.ALL) {
            int i = a.ordinal();
            boolean raised = shown[i] > base[i];
            boolean rowHover = Ui.hit(mx, my, x, y - 2, w, 13);
            if (rowHover) {
                Ui.band(c, x, y - 2, w, 13, Theme.ROW_HOVER);
            }
            Ui.diamond(c, x + 4, y + 3, 2, Theme.opaque(a.color));
            c.text(c.tr(a.langKey()), x + 11, y, Theme.TEXT, false);
            int valueX = steppers ? x + w - 26 : x + w;
            Ui.right(c, String.valueOf(shown[i]), valueX, y, raised ? Theme.BLUE : Theme.TEXT, false);
            if (steppers) {
                Ui.stepper(c, x + w - 24, y - 2, "-", Ui.hit(mx, my, x + w - 24, y - 2, 11, 11), shown[i] > base[i]);
                Ui.stepper(c, x + w - 11, y - 2, "+", Ui.hit(mx, my, x + w - 11, y - 2, 11, 11), shown[i] < Attr.MAX);
            }
            y += 13;
        }
        return y;
    }

    /** Index of the attribute stepper under the mouse: {@code +attr+1} for plus, {@code -(attr+1)} for minus, 0 if none. */
    static int stepperAt(int x, int y, int w, double mx, double my) {
        for (Attr a : Attr.ALL) {
            int ry = y + a.ordinal() * 13 - 2;
            if (Ui.hit(mx, my, x + w - 24, ry, 11, 11)) {
                return -(a.ordinal() + 1);
            }
            if (Ui.hit(mx, my, x + w - 11, ry, 11, 11)) {
                return a.ordinal() + 1;
            }
        }
        return 0;
    }

    /** Derived stats; {@code before} may be null. Returns y below. */
    static int derived(Canvas c, int x, int y, int w, int[] before, int[] after) {
        y = stat(c, x, y, w, Lang.HEALTH, Stats.maxHealth(get(before, Attr.VIGOR)) * Rb.DISPLAY_SCALE,
                Stats.maxHealth(after[Attr.VIGOR.ordinal()]) * Rb.DISPLAY_SCALE, before != null, "%.0f");
        y = stat(c, x, y, w, Lang.FOCUS, Stats.maxFocus(get(before, Attr.MIND)), Stats.maxFocus(after[Attr.MIND.ordinal()]),
                before != null, "%.0f");
        y = stat(c, x, y, w, Lang.STAMINA, Stats.maxStamina(get(before, Attr.ENDURANCE)),
                Stats.maxStamina(after[Attr.ENDURANCE.ordinal()]), before != null, "%.0f");
        y = stat(c, x, y, w, Lang.POISE, Stats.poise(get(before, Attr.ENDURANCE)) * 100,
                Stats.poise(after[Attr.ENDURANCE.ordinal()]) * 100, before != null, "%.0f%%");
        y = stat(c, x, y, w, Lang.DISCOVERY, 100 + Stats.discovery(get(before, Attr.ARCANE)) * 10,
                100 + Stats.discovery(after[Attr.ARCANE.ordinal()]) * 10, before != null, "%.0f");
        y = stat(c, x, y, w, Lang.WARDING, Stats.warding(get(before, Attr.INTELLIGENCE)) * 100,
                Stats.warding(after[Attr.INTELLIGENCE.ordinal()]) * 100, before != null, "%.0f%%");
        y = stat(c, x, y, w, Lang.BLESSING, Stats.blessing(get(before, Attr.FAITH)) * 100,
                Stats.blessing(after[Attr.FAITH.ordinal()]) * 100, before != null, "+%.0f%%");
        y = stat(c, x, y, w, Lang.POTENCY, Stats.potency(get(before, Attr.ARCANE)) * 100,
                Stats.potency(after[Attr.ARCANE.ordinal()]) * 100, before != null, "%.0f%%");
        return y;
    }

    private static int get(int[] attrs, Attr a) {
        return attrs == null ? 10 : attrs[a.ordinal()];
    }

    private static int stat(Canvas c, int x, int y, int w, String key, double before, double after, boolean compare,
                            String fmt) {
        c.text(c.tr(key), x, y, Theme.TEXT_DIM, false);
        String now = String.format(Locale.ROOT, fmt, after);
        int color = Theme.TEXT;
        if (compare && Math.round(after * 10) != Math.round(before * 10)) {
            color = after > before ? Theme.BLUE : Theme.BAD;
            String old = String.format(Locale.ROOT, fmt, before);
            Ui.right(c, old + " §8→ ", x + w - c.textWidth(now), y, Theme.TEXT_FAINT, false);
        }
        Ui.right(c, now, x + w, y, color, false);
        return y + 11;
    }

    /** Attack power block for a held weapon; compares when {@code before} is given. Returns y below. */
    static int attackPower(Canvas c, int x, int y, int w, ItemView held, int[] before, int[] after) {
        WeaponSpec spec = held == null ? null : held.weapon();
        if (spec == null) {
            c.text(c.tr(Lang.ATTACK_POWER), x, y, Theme.TEXT_DIM, false);
            Ui.right(c, "-", x + w, y, Theme.TEXT_FAINT, false);
            return y + 11;
        }
        AttackRating now = AttackRating.of(spec, held.upgrade(), held.affinity(), after);
        AttackRating old = before == null ? null : AttackRating.of(spec, held.upgrade(), held.affinity(), before);
        if (spec.cls.isCatalyst()) {
            return compareRow(c, x, y, w, c.tr(Lang.TT_SPELL_POWER), old == null ? -1 : old.spellPower, now.spellPower);
        }
        for (DamageType t : DamageType.ALL) {
            if (now.base[t.ordinal()] <= 0) {
                continue;
            }
            y = compareRow(c, x, y, w, c.tr(t.langKey()), old == null ? -1 : old.total(t) * Rb.DISPLAY_SCALE,
                    now.total(t) * Rb.DISPLAY_SCALE);
        }
        return y;
    }

    /** Attack power of the same weapon in two states (e.g. before and after reinforcement). */
    static int attackPowerCompare(Canvas c, int x, int y, int w, ItemView before, ItemView after, int[] attrs) {
        WeaponSpec spec = after.weapon();
        if (spec == null) {
            return y;
        }
        AttackRating now = AttackRating.of(spec, after.upgrade(), after.affinity(), attrs);
        AttackRating old = AttackRating.of(spec, before.upgrade(), before.affinity(), attrs);
        if (spec.cls.isCatalyst()) {
            return compareRow(c, x, y, w, c.tr(Lang.TT_SPELL_POWER), old.spellPower, now.spellPower);
        }
        for (DamageType t : DamageType.ALL) {
            if (now.base[t.ordinal()] <= 0 && old.base[t.ordinal()] <= 0) {
                continue;
            }
            y = compareRow(c, x, y, w, c.tr(t.langKey()), old.total(t) * Rb.DISPLAY_SCALE, now.total(t) * Rb.DISPLAY_SCALE);
        }
        return y;
    }

    /**
     * Scaling letters before and after a change of affinity. Attributes that scale in neither state are left out,
     * and the rest flow into two columns when they would run past {@code maxY}.
     */
    static int scalingCompare(Canvas c, int x, int y, int w, int maxY, ItemView before, ItemView after, int[] attrs) {
        WeaponSpec spec = after.weapon();
        if (spec == null) {
            return y;
        }
        AttackRating now = AttackRating.of(spec, after.upgrade(), after.affinity(), attrs);
        AttackRating old = AttackRating.of(spec, before.upgrade(), before.affinity(), attrs);
        sectionLabel(c, c.tr(Lang.TT_SCALING), x, y, w);
        y += 13;
        List<Attr> shown = new ArrayList<>();
        for (Attr a : new Attr[]{Attr.STRENGTH, Attr.DEXTERITY, Attr.INTELLIGENCE, Attr.FAITH, Attr.ARCANE}) {
            if (old.grade(a) != Grade.NONE || now.grade(a) != Grade.NONE) {
                shown.add(a);
            }
        }
        int fit = Math.max(1, (maxY - y) / 11);
        int cols = shown.size() > fit ? 2 : 1;
        int rows = (shown.size() + cols - 1) / cols;
        int cw = cols == 1 ? w : (w - 8) / 2;
        for (int i = 0; i < shown.size(); i++) {
            Attr a = shown.get(i);
            int cx = x + (i / rows) * (cw + 8);
            int cy = y + (i % rows) * 11;
            var g0 = old.grade(a);
            var g1 = now.grade(a);
            c.text(c.tr(a.shortKey()), cx, cy, Theme.TEXT_DIM, false);
            String now1 = g1.letter;
            int color = Theme.opaque(g1.color);
            if (g0 != g1) {
                Ui.right(c, g0.letter + " §8→ ", cx + cw - c.textWidth(now1), cy, Theme.TEXT_FAINT, false);
                color = g1.ordinal() > g0.ordinal() ? Theme.BLUE : Theme.BAD;
            }
            Ui.right(c, now1, cx + cw, cy, color, false);
        }
        return y + rows * 11;
    }

    private static int compareRow(Canvas c, int x, int y, int w, String label, double before, double after) {
        c.text(label, x, y, Theme.TEXT_DIM, false);
        String now = String.valueOf(Math.round(after));
        int color = Theme.TEXT;
        if (before >= 0 && Math.round(before) != Math.round(after)) {
            color = after > before ? Theme.BLUE : Theme.BAD;
            Ui.right(c, Math.round(before) + " §8→ ", x + w - c.textWidth(now), y, Theme.TEXT_FAINT, false);
        }
        Ui.right(c, now, x + w, y, color, false);
        return y + 11;
    }

    private Kit() {
    }
}
