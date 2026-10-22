package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.AttackRating;
import io.nightbeam.runeborn.core.weapon.DamageType;
import io.nightbeam.runeborn.core.weapon.Grade;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** Builds tooltip lines (with § codes) shared by every version's tooltip hook. */
public final class Tooltips {
    /** "Heavy Highland Claymore +7", without color. */
    public static String weaponName(WeaponSpec spec, int upgrade, Affinity affinity, Function<String, String> tr) {
        StringBuilder sb = new StringBuilder();
        if (affinity != Affinity.STANDARD && spec.infusable()) {
            sb.append(tr.apply(affinity.langKey())).append(' ');
        }
        sb.append(tr.apply(spec.langKey()));
        if (upgrade > 0) {
            sb.append(" +").append(upgrade);
        }
        return sb.toString();
    }

    /**
     * @param attrs effective player attributes, or null when unknown (e.g. no synced data yet)
     * @param detail full breakdown (shift held)
     */
    public static List<String> weapon(WeaponSpec spec, int upgrade, Affinity affinity, int[] attrs, boolean detail,
                                      Function<String, String> tr) {
        List<String> lines = new ArrayList<>();
        int[] a = attrs != null ? attrs : io.nightbeam.runeborn.core.player.PlayerData.defaultAttrs();
        AttackRating ar = AttackRating.of(spec, upgrade, affinity, a);

        lines.add("§8" + tr.apply(spec.cls.langKey()) + " · " + rarityColor(spec) + tr.apply(spec.rarity.langKey())
                + " §8· §7+" + upgrade + "/" + spec.path.max);

        if (spec.cls.isCatalyst()) {
            lines.add("§6" + tr.apply(Lang.TT_SPELL_POWER) + " §f" + Math.round(ar.spellPower));
        } else {
            lines.add("§6" + tr.apply(Lang.TT_ATTACK) + " §f" + Math.round(ar.total * Rb.DISPLAY_SCALE));
            if (detail) {
                for (DamageType t : DamageType.ALL) {
                    double base = ar.base[t.ordinal()];
                    if (base <= 0) {
                        continue;
                    }
                    double bonus = ar.anyUnmet ? 0 : ar.bonus[t.ordinal()];
                    lines.add("  " + color(t.color) + tr.apply(t.langKey()) + " §f" + Math.round(base * Rb.DISPLAY_SCALE)
                            + (bonus > 0.05 ? " §9+ " + Math.round(bonus * Rb.DISPLAY_SCALE) : ""));
                }
            }
        }

        StringBuilder scaling = new StringBuilder("§7" + tr.apply(Lang.TT_SCALING) + " ");
        boolean any = false;
        for (Attr attr : new Attr[]{Attr.STRENGTH, Attr.DEXTERITY, Attr.INTELLIGENCE, Attr.FAITH, Attr.ARCANE}) {
            Grade g = ar.grade(attr);
            if (g == Grade.NONE) {
                continue;
            }
            scaling.append("§8").append(tr.apply(attr.shortKey())).append(' ').append(color(g.color)).append(g.letter).append("  ");
            any = true;
        }
        if (any) {
            lines.add(scaling.toString().trim());
        }

        StringBuilder req = new StringBuilder("§7" + tr.apply(Lang.TT_REQUIRES) + " ");
        boolean anyReq = false;
        for (Attr attr : Attr.ALL) {
            int r = spec.requirement(attr);
            if (r <= 0) {
                continue;
            }
            boolean unmet = attrs != null && attrs[attr.ordinal()] < r;
            req.append("§8").append(tr.apply(attr.shortKey())).append(' ').append(unmet ? "§c" : "§f").append(r).append("  ");
            anyReq = true;
        }
        if (anyReq) {
            lines.add(req.toString().trim());
        }

        StringBuilder status = new StringBuilder();
        for (StatusType s : StatusType.ALL) {
            int v = ar.status[s.ordinal()];
            if (v > 0) {
                status.append(color(s.color)).append(tr.apply(s.langKey())).append(" §f").append(v).append("  ");
            }
        }
        if (status.length() > 0) {
            lines.add("§7" + tr.apply(Lang.TT_PASSIVE) + " " + status.toString().trim());
        }

        if (spec.art == Art.SPELLCAST && spec.spell != null) {
            lines.add("§b" + tr.apply(Lang.TT_SPELL) + ": §f" + tr.apply(spec.spell.langKey()) + " §8(" + spec.spell.fp + " "
                    + tr.apply(Lang.TT_FP) + ")");
            if (detail) {
                lines.add("  §7§o" + tr.apply(spec.spell.descKey()));
            }
        } else {
            lines.add("§b" + tr.apply(Lang.TT_ART) + ": §f" + tr.apply(spec.art.langKey()) + " §8(" + spec.art.fp + " "
                    + tr.apply(Lang.TT_FP) + ")");
            if (detail) {
                lines.add("  §7§o" + tr.apply(spec.art.descKey()));
            }
        }

        if (detail) {
            lines.add("§8" + tr.apply(Lang.TT_SPEED) + " §7" + String.format(Locale.ROOT, "%.2f", spec.cls.speed)
                    + "  §8" + tr.apply(Lang.TT_STAMINA) + " §7" + spec.cls.stamina);
            for (int i = 0; i < spec.lore.size(); i++) {
                lines.add("§8§o" + tr.apply(spec.langKey() + ".lore" + i));
            }
        } else {
            lines.add("§8" + tr.apply(Lang.TT_SHIFT));
        }
        if (ar.anyUnmet && attrs != null) {
            lines.add("§c" + tr.apply(Lang.TT_UNMET));
        }
        return lines;
    }

    /** Lines for vanilla and third-party melee weapons, which also scale. */
    public static List<String> implicit(WeaponSpec implicit, int[] attrs, Function<String, String> tr) {
        List<String> lines = new ArrayList<>();
        AttackRating ar = AttackRating.of(implicit, 0, Affinity.STANDARD, attrs);
        lines.add("§6" + tr.apply(Lang.TT_ATTACK) + " §f" + Math.round(ar.total * Rb.DISPLAY_SCALE) + " §8("
                + tr.apply(Lang.TT_IMPLICIT) + ")");
        return lines;
    }

    public static List<String> material(MaterialSpec spec, Function<String, String> tr) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < spec.lore().size(); i++) {
            lines.add((i == 0 ? "§7" : "§8") + tr.apply(spec.langKey() + ".lore" + i));
        }
        return lines;
    }

    public static String rarityColor(WeaponSpec spec) {
        return spec.rarity.code;
    }

    /** Nearest legacy formatting code for an RGB color. */
    public static String color(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int[][] palette = {
                {0xAA, 0x00, 0x00, '4'}, {0xFF, 0x55, 0x55, 'c'}, {0xFF, 0xAA, 0x00, '6'}, {0xFF, 0xFF, 0x55, 'e'},
                {0x00, 0xAA, 0x00, '2'}, {0x55, 0xFF, 0x55, 'a'}, {0x55, 0xFF, 0xFF, 'b'}, {0x00, 0xAA, 0xAA, '3'},
                {0x55, 0x55, 0xFF, '9'}, {0xFF, 0x55, 0xFF, 'd'}, {0xAA, 0x00, 0xAA, '5'}, {0xFF, 0xFF, 0xFF, 'f'},
                {0xAA, 0xAA, 0xAA, '7'}, {0x55, 0x55, 0x55, '8'}
        };
        int best = 0;
        long bestDist = Long.MAX_VALUE;
        for (int i = 0; i < palette.length; i++) {
            long dr = r - palette[i][0];
            long dg = g - palette[i][1];
            long db = b - palette[i][2];
            long dist = dr * dr * 3 + dg * dg * 4 + db * db * 2;
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return "§" + (char) palette[best][3];
    }

    private Tooltips() {
    }
}
