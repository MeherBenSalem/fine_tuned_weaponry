package io.nightbeam.runeborn.core.boss;

/** Scripted boss attacks. {@code cooldown} in ticks; {@code minPhase} 1 or 2 (phase 2 starts at half health). */
public enum Ability {
    GROUND_SLAM(120, 1, "slams the earth"),
    SUMMON_HOLLOWS(400, 1, "calls the hollow"),
    ENRAGE(0, 2, "is enraged"),
    FROST_NOVA(160, 1, "unleashes a frost nova"),
    ICE_VOLLEY(100, 1, "looses an ice volley"),
    BLINK(140, 1, "blinks"),
    LIGHTNING_BARRAGE(140, 1, "calls the storm"),
    STORM_DASH(120, 2, "rides the lightning"),
    BLOOD_FLURRY(100, 1, "dances in blood"),
    BLOOD_BLOOM(300, 2, "blooms"),
    METEOR_RAIN(220, 1, "rains stars"),
    FIRE_PILLARS(140, 1, "raises pillars of flame"),
    ASH_CLOUD(300, 2, "exhales ash"),
    CHARGE(120, 1, "charges"),
    ROAR(260, 1, "roars"),
    NIGHTFALL(400, 1, "brings the night");

    public final int cooldown;
    public final int minPhase;
    public final String verb;

    Ability(int cooldown, int minPhase, String verb) {
        this.cooldown = cooldown;
        this.minPhase = minPhase;
        this.verb = verb;
    }
}
