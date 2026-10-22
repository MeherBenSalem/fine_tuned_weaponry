package io.nightbeam.runeborn.core.stat;

/** Rune level math. Level is derived from attributes: a character with every attribute at 10 is level 1. */
public final class Leveling {
    public static final int BASE_SUM = 79;
    public static final int MAX_LEVEL = Attr.MAX * Attr.COUNT - BASE_SUM;

    public static int level(int[] attrs) {
        int sum = 0;
        for (int v : attrs) {
            sum += v;
        }
        return Math.max(1, sum - BASE_SUM);
    }

    /** Runes required to go from {@code level} to {@code level + 1}, before the config multiplier. */
    public static long cost(int level) {
        int l = Math.max(1, level) + 81;
        double x = Math.max(0, l - 92) * 0.02;
        return (long) Math.floor((x + 0.1) * l * l) + 1;
    }

    public static long cost(int level, double multiplier) {
        return Math.max(1, Math.round(cost(level) * multiplier));
    }

    /** Total runes to buy {@code steps} levels starting from {@code level}. */
    public static long costRange(int level, int steps, double multiplier) {
        long total = 0;
        for (int i = 0; i < steps; i++) {
            total += cost(level + i, multiplier);
        }
        return total;
    }

    private Leveling() {
    }
}
