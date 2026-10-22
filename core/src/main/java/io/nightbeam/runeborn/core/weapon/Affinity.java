package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.stat.Attr;

/**
 * Infusions applied with whetstones at a shrine. An affinity rewrites a standard weapon's
 * damage split and scaling; lustrous (unique) weapons cannot be infused.
 */
public enum Affinity {
    STANDARD(0xD8D2C4),
    HEAVY(0xC98B3C),
    KEEN(0xD8C55A),
    QUALITY(0xE8E0C8),
    MAGIC(0x6FA8E8),
    FIRE(0xE8743C),
    FLAME_ART(0xF2A65A),
    LIGHTNING(0xE8D35A),
    SACRED(0xF2E3A8),
    COLD(0x9ED8F0),
    BLOOD(0xB3262B),
    POISON(0x7BAA3A),
    OCCULT(0x9C7FB8);

    public static final Affinity[] ALL = values();

    public final int color;

    Affinity(int color) {
        this.color = color;
    }

    public String id() {
        return name().toLowerCase();
    }

    public String langKey() {
        return "runeborn.affinity." + id();
    }

    public static Affinity byId(String id) {
        for (Affinity a : ALL) {
            if (a.id().equals(id)) {
                return a;
            }
        }
        return STANDARD;
    }

    /** Applies this affinity to a standard weapon profile. Arrays are modified in place. */
    public void apply(double[] damage, double[] scaling, int[] status) {
        double phys = damage[DamageType.PHYSICAL.ordinal()];
        double str = scaling[Attr.STRENGTH.ordinal()];
        double dex = scaling[Attr.DEXTERITY.ordinal()];
        double best = Math.max(str, dex);
        switch (this) {
            case STANDARD -> {
            }
            case HEAVY -> {
                damage[0] = phys * 1.08;
                set(scaling, Attr.STRENGTH, best * 1.6 + 0.2);
                set(scaling, Attr.DEXTERITY, 0);
            }
            case KEEN -> {
                damage[0] = phys * 1.05;
                set(scaling, Attr.DEXTERITY, best * 1.6 + 0.2);
                set(scaling, Attr.STRENGTH, 0);
            }
            case QUALITY -> {
                set(scaling, Attr.STRENGTH, best * 1.05 + 0.1);
                set(scaling, Attr.DEXTERITY, best * 1.05 + 0.1);
            }
            case MAGIC -> {
                damage[0] = phys * 0.7;
                damage[DamageType.MAGIC.ordinal()] += phys * 0.55;
                set(scaling, Attr.INTELLIGENCE, 1.1);
                set(scaling, Attr.STRENGTH, str * 0.5);
                set(scaling, Attr.DEXTERITY, dex * 0.5);
            }
            case FIRE -> {
                damage[0] = phys * 0.75;
                damage[DamageType.FIRE.ordinal()] += phys * 0.55;
                set(scaling, Attr.STRENGTH, Math.max(str, 0.9));
                set(scaling, Attr.DEXTERITY, dex * 0.3);
            }
            case FLAME_ART -> {
                damage[0] = phys * 0.75;
                damage[DamageType.FIRE.ordinal()] += phys * 0.5;
                set(scaling, Attr.FAITH, 1.0);
                set(scaling, Attr.STRENGTH, str * 0.5);
                set(scaling, Attr.DEXTERITY, dex * 0.5);
            }
            case LIGHTNING -> {
                damage[0] = phys * 0.75;
                damage[DamageType.LIGHTNING.ordinal()] += phys * 0.55;
                set(scaling, Attr.DEXTERITY, Math.max(dex, 1.0));
                set(scaling, Attr.STRENGTH, str * 0.3);
            }
            case SACRED -> {
                damage[0] = phys * 0.75;
                damage[DamageType.HOLY.ordinal()] += phys * 0.55;
                set(scaling, Attr.FAITH, 1.0);
                set(scaling, Attr.STRENGTH, str * 0.5);
                set(scaling, Attr.DEXTERITY, dex * 0.5);
            }
            case COLD -> {
                damage[0] = phys * 0.85;
                damage[DamageType.MAGIC.ordinal()] += phys * 0.4;
                set(scaling, Attr.INTELLIGENCE, 0.9);
                status[StatusType.FROST.ordinal()] = Math.max(status[StatusType.FROST.ordinal()], 45);
            }
            case BLOOD -> {
                damage[0] = phys * 0.9;
                set(scaling, Attr.ARCANE, 0.8);
                status[StatusType.BLEED.ordinal()] = Math.max(status[StatusType.BLEED.ordinal()], 50);
            }
            case POISON -> {
                damage[0] = phys * 0.9;
                set(scaling, Attr.ARCANE, 0.8);
                status[StatusType.POISON.ordinal()] = Math.max(status[StatusType.POISON.ordinal()], 60);
            }
            case OCCULT -> {
                damage[0] = phys * 0.95;
                set(scaling, Attr.ARCANE, 1.0);
                for (int i = 0; i < status.length; i++) {
                    status[i] = (int) Math.round(status[i] * 1.3);
                }
            }
        }
    }

    private static void set(double[] scaling, Attr attr, double value) {
        scaling[attr.ordinal()] = value;
    }
}
