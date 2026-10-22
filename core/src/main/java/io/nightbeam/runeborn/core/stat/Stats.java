package io.nightbeam.runeborn.core.stat;

/**
 * Derived character values. Every curve has soft caps (diminishing returns) the way
 * souls-like games do, so spreading points stays a real decision.
 */
public final class Stats {
    /** Total max health (vanilla health points, 20 = ten hearts). */
    private static final Curve HEALTH = Curve.of(1, 11, 10, 20, 25, 35, 40, 47, 60, 57, 99, 61);
    private static final Curve FOCUS = Curve.of(1, 44, 10, 80, 15, 100, 35, 230, 60, 350, 99, 450);
    private static final Curve STAMINA = Curve.of(1, 80, 10, 98, 15, 108, 35, 140, 60, 155, 99, 160);
    private static final Curve POISE = Curve.of(10, 0, 60, 0.25, 99, 0.30);
    private static final Curve DISCOVERY = Curve.of(10, 0, 60, 3.0, 99, 4.0);
    private static final Curve WARDING = Curve.of(10, 0, 60, 0.15, 99, 0.20);
    private static final Curve BLESSING = Curve.of(10, 0, 60, 0.30, 99, 0.40);
    private static final Curve POTENCY = Curve.of(10, 1.0, 60, 1.45, 99, 1.6);
    private static final Curve ROBUSTNESS = Curve.of(1, 80, 10, 100, 40, 160, 60, 200, 99, 230);

    public static double maxHealth(int vigor) {
        return HEALTH.at(vigor);
    }

    public static double maxFocus(int mind) {
        return FOCUS.at(mind);
    }

    public static double maxStamina(int endurance) {
        return STAMINA.at(endurance);
    }

    /** Knockback resistance fraction from Endurance. */
    public static double poise(int endurance) {
        return POISE.at(endurance);
    }

    /** Luck points from Arcane; feeds loot rolls. */
    public static double discovery(int arcane) {
        return DISCOVERY.at(arcane);
    }

    /** Fraction of incoming magic damage negated from Intelligence. */
    public static double warding(int intelligence) {
        return WARDING.at(intelligence);
    }

    /** Bonus fraction on healing received from Faith. */
    public static double blessing(int faith) {
        return BLESSING.at(faith);
    }

    /** Multiplier on status buildup inflicted, from Arcane. */
    public static double potency(int arcane) {
        return POTENCY.at(arcane);
    }

    /** Status buildup a player can absorb before it procs. */
    public static double robustness(int stat) {
        return ROBUSTNESS.at(stat);
    }

    /**
     * Fraction (0..1) of a weapon's scaling coefficient realised at this attribute value.
     * Soft caps at 18, 60 and 80, mirroring the classic "calc correct" shape.
     */
    public static double scalingCurve(int value) {
        double x = Math.max(1, Math.min(99, value));
        if (x <= 18) {
            return 0.25 * Math.pow((x - 1) / 17.0, 1.2);
        }
        if (x <= 60) {
            return 0.25 + 0.5 * (1 - Math.pow(1 - (x - 18) / 42.0, 1.2));
        }
        if (x <= 80) {
            return 0.75 + 0.15 * ((x - 60) / 20.0);
        }
        return 0.9 + 0.1 * (1 - Math.pow(1 - (x - 80) / 19.0, 1.2));
    }

    private Stats() {
    }
}
