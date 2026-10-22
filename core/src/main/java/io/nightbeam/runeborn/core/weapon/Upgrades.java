package io.nightbeam.runeborn.core.weapon;

/** Materials and runes needed to reinforce a weapon to the next level. */
public final class Upgrades {
    public record Cost(String materialId, int count, long runes) {
    }

    /** Cost to go from {@code current} to {@code current + 1}, or {@code null} when maxed. */
    public static Cost next(WeaponSpec spec, int current, double runeMultiplier) {
        int target = current + 1;
        if (target > spec.path.max) {
            return null;
        }
        if (spec.path == WeaponSpec.Path.STANDARD) {
            long runes = Math.round((120 + 90L * target) * runeMultiplier);
            if (target == 25) {
                return new Cost("sovereign_tempering_stone", 1, runes);
            }
            int tier = (target + 2) / 3;
            int count = ((target - 1) % 3 + 1) * 2;
            return new Cost("tempering_stone_" + tier, count, runes);
        }
        long runes = Math.round((600 + 500L * target) * runeMultiplier);
        if (target == 10) {
            return new Cost("sovereign_lustrous_stone", 1, runes);
        }
        return new Cost("lustrous_stone_" + target, 1, runes);
    }

    public static long infusionCost(double runeMultiplier) {
        return Math.round(1000 * runeMultiplier);
    }

    private Upgrades() {
    }
}
