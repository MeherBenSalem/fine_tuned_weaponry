package io.nightbeam.runeborn.core.weapon;

/** Status effects that build up on a target and burst when the meter fills. */
public enum StatusType {
    /** Burst of damage relative to max health. */
    BLEED(0xB3262B),
    /** Burst of damage plus slowness and increased damage taken. */
    FROST(0x9ED8F0),
    /** Lingering poison. */
    POISON(0x7BAA3A),
    /** Withering rot that eats through health. */
    BLIGHT(0xC2603A);

    public static final StatusType[] ALL = values();

    public final int color;

    StatusType(int color) {
        this.color = color;
    }

    public String langKey() {
        return "runeborn.status." + name().toLowerCase();
    }
}
