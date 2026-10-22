package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;

/**
 * Attack rating for a weapon in a given state against a set of attributes.
 * All damage values are vanilla units (2.0 = one heart).
 */
public final class AttackRating {
    /** Multiplier applied when any requirement is unmet. */
    public static final double UNMET_PENALTY = 0.6;

    public final WeaponSpec spec;
    public final int upgrade;
    public final Affinity affinity;
    /** Per damage type, after upgrades. */
    public final double[] base = new double[DamageType.ALL.length];
    /** Per damage type, from attribute scaling. */
    public final double[] bonus = new double[DamageType.ALL.length];
    /** Effective coefficient per attribute (after affinity and upgrades), for grade letters. */
    public final double[] scaling = new double[Attr.COUNT];
    /** Effective status buildup per hit. */
    public final int[] status = new int[StatusType.ALL.length];
    public final boolean[] unmet = new boolean[Attr.COUNT];
    public final boolean anyUnmet;
    public final double total;
    /** Spell power for catalysts (100 = unscaled), 0 for other weapons. */
    public final double spellPower;

    private AttackRating(WeaponSpec spec, int upgrade, Affinity affinity, int[] attrs) {
        this.spec = spec;
        this.upgrade = Math.max(0, Math.min(upgrade, spec.path.max));
        this.affinity = spec.infusable() ? affinity : Affinity.STANDARD;

        double[] dmg = spec.damageArray();
        double[] coef = spec.scalingArray();
        int[] stat = spec.statusArray();
        this.affinity.apply(dmg, coef, stat);

        double progress = (double) this.upgrade / spec.path.max;
        double upgradeFactor = upgradeFactor(spec.path, this.upgrade);
        for (int i = 0; i < Attr.COUNT; i++) {
            scaling[i] = coef[i] * (1 + 0.5 * progress);
        }
        for (int i = 0; i < stat.length; i++) {
            status[i] = (int) Math.round(stat[i] * (1 + 0.4 * progress));
        }

        boolean missing = false;
        for (Attr a : Attr.ALL) {
            if (attrs[a.ordinal()] < spec.requirement(a)) {
                unmet[a.ordinal()] = true;
                missing = true;
            }
        }
        this.anyUnmet = missing;

        double sum = 0;
        for (DamageType t : DamageType.ALL) {
            double b = dmg[t.ordinal()] * upgradeFactor;
            base[t.ordinal()] = b;
            if (b <= 0) {
                continue;
            }
            double extra = 0;
            if (!missing) {
                for (Attr a : t.scaledBy) {
                    extra += b * scaling[a.ordinal()] * Stats.scalingCurve(attrs[a.ordinal()]);
                }
            }
            bonus[t.ordinal()] = extra;
            sum += b + extra;
        }
        this.total = missing ? sum * UNMET_PENALTY : sum;

        if (spec.cls.isCatalyst()) {
            double power = 0;
            for (Attr a : Attr.ALL) {
                power += scaling[a.ordinal()] * Stats.scalingCurve(attrs[a.ordinal()]);
            }
            double sp = 100 * upgradeFactor * (1 + power);
            this.spellPower = missing ? sp * UNMET_PENALTY : sp;
        } else {
            this.spellPower = 0;
        }
    }

    public static AttackRating of(WeaponSpec spec, int upgrade, Affinity affinity, int[] attrs) {
        return new AttackRating(spec, upgrade, affinity, attrs);
    }

    public static double upgradeFactor(WeaponSpec.Path path, int upgrade) {
        return path == WeaponSpec.Path.STANDARD ? 1 + 0.058 * upgrade : 1 + 0.145 * upgrade;
    }

    public double total(DamageType type) {
        double value = base[type.ordinal()] + bonus[type.ordinal()];
        return anyUnmet ? value * UNMET_PENALTY : value;
    }

    /** Share (0..1) of the total that is of the given type. */
    public double share(DamageType type) {
        double all = 0;
        for (DamageType t : DamageType.ALL) {
            all += total(t);
        }
        return all <= 0 ? 0 : total(type) / all;
    }

    public Grade grade(Attr attr) {
        return Grade.of(scaling[attr.ordinal()]);
    }
}
