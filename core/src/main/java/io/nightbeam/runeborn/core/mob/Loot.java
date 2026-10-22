package io.nightbeam.runeborn.core.mob;

import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.weapon.Rarity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Drop rolls. Returns item ids with counts; the Minecraft side turns them into item entities. */
public final class Loot {
    public record Drop(String id, int count) {
    }

    public static int temperingTier(int level) {
        return Math.max(1, Math.min(8, 1 + level / 8));
    }

    public static int lustrousTier(int level) {
        return Math.max(1, Math.min(9, 1 + level / 11));
    }

    /** @param luck discovery luck from Arcane; each point adds roughly 10% to drop chances */
    public static List<Drop> forMob(int level, boolean elite, double luck, Random random) {
        double luckMult = 1 + Math.max(0, luck) * 0.1;
        List<Drop> drops = new ArrayList<>();
        if (!elite) {
            if (random.nextDouble() < 0.06 * luckMult) {
                drops.add(new Drop("tempering_stone_" + temperingTier(level), 1));
            }
            if (random.nextDouble() < 0.008 * luckMult) {
                drops.add(new Drop(level >= 30 ? "lustrous_rune" : "faded_rune", 1));
            }
            if (level >= 20 && random.nextDouble() < 0.004 * luckMult) {
                drops.add(new Drop("lustrous_stone_" + lustrousTier(level), 1));
            }
            return drops;
        }
        drops.add(new Drop("tempering_stone_" + temperingTier(level), 2 + random.nextInt(3)));
        if (random.nextDouble() < 0.5 * luckMult) {
            drops.add(new Drop("sovereign_fragment", 1 + random.nextInt(2)));
        }
        if (random.nextDouble() < 0.25 * luckMult) {
            drops.add(new Drop("lustrous_stone_" + lustrousTier(level), 1));
        }
        if (random.nextDouble() < 0.35 * luckMult) {
            drops.add(new Drop(level >= 40 ? "radiant_rune" : level >= 15 ? "lustrous_rune" : "faded_rune", 1));
        }
        if (random.nextDouble() < 0.05 * luckMult) {
            drops.add(new Drop("sunseed", 1));
        }
        if (random.nextDouble() < 0.03 * luckMult) {
            drops.add(new Drop("hallowed_tear", 1));
        }
        if (random.nextDouble() < 0.02 * luckMult) {
            drops.add(new Drop("rebirth_tear", 1));
        }
        if (random.nextDouble() < 0.12 * luckMult) {
            WeaponSpec weapon = randomWeapon(level, random);
            if (weapon != null) {
                drops.add(new Drop(weapon.id, 1));
            }
        }
        return drops;
    }

    public static List<Drop> forBoss(BossSpec boss, Random random) {
        List<Drop> drops = new ArrayList<>();
        drops.add(new Drop(boss.relicId(), 1));
        if (boss.shard() != null) {
            drops.add(new Drop(boss.shardId(), 1));
        }
        int level = boss.level();
        drops.add(new Drop("lustrous_stone_" + lustrousTier(level), 2));
        drops.add(new Drop("tempering_stone_" + temperingTier(level), 6));
        drops.add(new Drop("sunseed", 1));
        drops.add(new Drop("hallowed_tear", 1));
        if (level >= 50) {
            drops.add(new Drop("sovereign_tempering_stone", 1));
        }
        if (level >= 70) {
            drops.add(new Drop("sovereign_lustrous_stone", 1));
        }
        drops.add(new Drop(level >= 50 ? "heros_rune" : "radiant_rune", 1 + random.nextInt(2)));
        if (random.nextDouble() < 0.5) {
            drops.add(new Drop("rebirth_tear", 1));
        }
        return drops;
    }

    private static WeaponSpec randomWeapon(int level, Random random) {
        List<WeaponSpec> pool = new ArrayList<>();
        for (WeaponSpec spec : Weapons.all()) {
            if (spec.rarity == Rarity.COMMON || (spec.rarity == Rarity.RARE && level >= 12)) {
                pool.add(spec);
                if (spec.rarity == Rarity.RARE && level >= 30) {
                    pool.add(spec);
                }
            }
        }
        return pool.isEmpty() ? null : pool.get(random.nextInt(pool.size()));
    }

    private Loot() {
    }
}
