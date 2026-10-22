package io.nightbeam.runeborn.core.art;

/**
 * Weapon arts: active skills triggered with the use key while holding a weapon.
 * {@code mult} scales the wielder's attack rating, {@code range} is in blocks.
 */
public enum Art {
    QUICKSTEP(Kind.DASH, 3, 10, 14, 0.0, 4.0, "Slip through an attack with a burst of untouchable speed."),
    UNSHEATHE(Kind.CONE_STRIKE, 6, 18, 40, 2.2, 4.5, "Gather focus, then cut everything before you in one draw."),
    CRIMSON_FLURRY(Kind.FLURRY, 8, 20, 50, 0.8, 3.5, "Three blood-soaked cuts that rapidly build bleed."),
    WAR_CRY(Kind.BUFF_ATTACK, 10, 10, 300, 0.0, 0.0, "A roar that hardens the body and sharpens every swing."),
    EARTHSHAKER(Kind.SLAM, 12, 26, 80, 1.8, 4.0, "Drive the weapon into the ground, flinging foes skyward."),
    LEAPING_CLEAVE(Kind.LEAP_SLAM, 10, 24, 70, 2.0, 3.0, "Leap forward and crash down with the full weight of steel."),
    IMPALING_THRUST(Kind.LINE, 6, 16, 40, 1.6, 7.0, "A lunging thrust that pierces every foe in a line."),
    SPINNING_REAP(Kind.SPIN, 10, 22, 60, 1.5, 3.5, "A wide circular reap that doubles status buildup."),
    WHIRLWIND(Kind.MULTI_SPIN, 12, 26, 80, 0.9, 3.0, "Spin three times, a storm of blades around you."),
    STARBLADE_VOLLEY(Kind.VOLLEY, 14, 12, 60, 1.0, 16.0, "Summon starblades that seek out up to three foes."),
    STORMCALL(Kind.STORM, 16, 16, 100, 1.6, 8.0, "Call lightning down upon nearby enemies."),
    RIMEFROST_STOMP(Kind.FROST_WAVE, 12, 20, 70, 1.2, 6.0, "Stamp a wave of rime that freezes all it touches."),
    EMBER_CLEAVE(Kind.FLAME_WAVE, 12, 22, 70, 1.5, 5.0, "A sweeping cleave that leaves a wake of fire."),
    SACRED_EDGE(Kind.BUFF_HOLY, 15, 8, 600, 0.3, 0.0, "Bless the blade with holy light for thirty seconds."),
    GRAVITY_WELL(Kind.PULL, 12, 14, 80, 0.8, 8.0, "Wrench nearby foes toward you with crushing gravity."),
    LUNAR_WAVE(Kind.WAVE, 20, 20, 60, 2.4, 14.0, "Release a crescent of moonlight that cleaves through ranks."),
    VOIDSTAR_FALL(Kind.METEOR, 25, 24, 160, 3.0, 4.5, "Pull a burning star from the void onto your target."),
    BLOOMWARD(Kind.HEAL_AURA, 20, 10, 300, 0.3, 8.0, "Scatter healing petals over you and your allies."),
    GRAVE_HARVEST(Kind.DRAIN_SPIN, 18, 24, 90, 1.8, 4.5, "Reap the living around you and drink their vigor."),
    THUNDERSTEP(Kind.THUNDER_DASH, 14, 18, 70, 1.6, 8.0, "Become lightning and tear through enemies in your path."),
    SHADOWSTEP(Kind.BLINK_STRIKE, 10, 12, 80, 2.5, 12.0, "Vanish and strike your target from behind."),
    FRENZIED_STRIKES(Kind.FLURRY5, 8, 24, 60, 0.6, 3.0, "Five savage blows in a frenzy."),
    RUINOUS_CHARGE(Kind.CHARGE, 14, 28, 90, 2.0, 6.0, "Charge forward, bowling over everything in your way."),
    SPELLCAST(Kind.CAST, 0, 0, 0, 0.0, 0.0, "Cast the catalyst's bound spell.");

    public enum Kind {
        DASH, CONE_STRIKE, FLURRY, BUFF_ATTACK, SLAM, LEAP_SLAM, LINE, SPIN, MULTI_SPIN, VOLLEY, STORM,
        FROST_WAVE, FLAME_WAVE, BUFF_HOLY, PULL, WAVE, METEOR, HEAL_AURA, DRAIN_SPIN, THUNDER_DASH,
        BLINK_STRIKE, FLURRY5, CHARGE, CAST
    }

    public final Kind kind;
    public final int fp;
    public final int stamina;
    public final int cooldown;
    public final double mult;
    public final double range;
    public final String description;

    Art(Kind kind, int fp, int stamina, int cooldown, double mult, double range, String description) {
        this.kind = kind;
        this.fp = fp;
        this.stamina = stamina;
        this.cooldown = cooldown;
        this.mult = mult;
        this.range = range;
        this.description = description;
    }

    public String id() {
        return name().toLowerCase();
    }

    public String langKey() {
        return "runeborn.art." + id();
    }

    public String descKey() {
        return "runeborn.art." + id() + ".desc";
    }

    public String englishName() {
        return Names.title(name());
    }

    public static Art byId(String id) {
        for (Art a : values()) {
            if (a.id().equals(id)) {
                return a;
            }
        }
        return null;
    }
}
