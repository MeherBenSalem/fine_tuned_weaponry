package io.nightbeam.runeborn.core.art;

import io.nightbeam.runeborn.core.stat.Attr;

/**
 * Spells bound to catalysts (staves for sorceries, seals for incantations).
 * Damage is {@code base * spellPower / 100}; {@code range} is in blocks.
 */
public enum Spell {
    STARSHARD_BOLT(School.SORCERY, Kind.BOLT, 7, 14, 4.0, 24, 8, "Fire a shard of starlight at your foe."),
    STARSHARD_BARRAGE(School.SORCERY, Kind.BARRAGE, 18, 40, 2.6, 24, 18, "Loose a rapid barrage of five star shards."),
    FROST_COMET(School.SORCERY, Kind.FROST_BURST, 16, 40, 5.0, 24, 16, "Hurl a comet of rime that shatters into frost."),
    ABYSSAL_RAY(School.SORCERY, Kind.RAY, 30, 70, 9.0, 32, 30, "Project a ray of the abyss that pierces all."),
    STORM_BOLT(School.SORCERY, Kind.CHAIN_LIGHTNING, 14, 30, 5.0, 24, 14, "Lightning that leaps between three foes."),
    GRAVITY_ORB(School.SORCERY, Kind.GRAVITY_BURST, 15, 40, 4.0, 20, 20, "Crush a crowd with a collapsing gravity orb."),
    BLESSED_FLAME(School.INCANTATION, Kind.FIRE_BOLT, 8, 16, 4.5, 20, 8, "Cast a blessed flame that sets foes ablaze."),
    MENDING_LIGHT(School.INCANTATION, Kind.HEAL, 25, 120, 0.4, 6, 12, "Heal yourself and nearby allies."),
    STORM_SPEAR(School.INCANTATION, Kind.LIGHTNING_SPEAR, 18, 40, 6.5, 32, 18, "Hurl a spear of storm that calls lightning."),
    PILLAR_OF_FLAME(School.INCANTATION, Kind.FLAME_PILLAR, 20, 50, 7.5, 24, 22, "Raise a pillar of flame beneath your foe."),
    BLOOD_SIGIL(School.INCANTATION, Kind.BLOOD_BURST, 12, 40, 3.0, 6, 14, "Pay in blood to rupture the veins of all nearby."),
    DRAGONFIRE(School.INCANTATION, Kind.BREATH, 26, 80, 1.6, 7, 26, "Exhale a torrent of draconic fire.");

    public enum School {
        SORCERY(Attr.INTELLIGENCE), INCANTATION(Attr.FAITH);

        public final Attr attr;

        School(Attr attr) {
            this.attr = attr;
        }
    }

    public enum Kind {
        BOLT, BARRAGE, FROST_BURST, RAY, CHAIN_LIGHTNING, GRAVITY_BURST, FIRE_BOLT, HEAL, LIGHTNING_SPEAR,
        FLAME_PILLAR, BLOOD_BURST, BREATH
    }

    public final School school;
    public final Kind kind;
    public final int fp;
    public final int cooldown;
    public final double base;
    public final double range;
    /** Minimum Intelligence (sorceries) or Faith (incantations). */
    public final int requirement;
    public final String description;

    Spell(School school, Kind kind, int fp, int cooldown, double base, double range, int requirement, String description) {
        this.school = school;
        this.kind = kind;
        this.fp = fp;
        this.cooldown = cooldown;
        this.base = base;
        this.range = range;
        this.requirement = requirement;
        this.description = description;
    }

    public String id() {
        return name().toLowerCase();
    }

    public String langKey() {
        return "runeborn.spell." + id();
    }

    public String descKey() {
        return "runeborn.spell." + id() + ".desc";
    }

    public String englishName() {
        return Names.title(name());
    }
}
