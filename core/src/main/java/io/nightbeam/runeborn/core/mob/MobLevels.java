package io.nightbeam.runeborn.core.mob;

import io.nightbeam.runeborn.core.config.ServerConfig;

import java.util.Random;

/** Zone levels: hostile mobs grow stronger with distance from spawn and in harsher dimensions. */
public final class MobLevels {
    public enum Realm {
        OVERWORLD, NETHER, END, OTHER;

        public static Realm of(String dimensionId) {
            return switch (dimensionId) {
                case "minecraft:overworld" -> OVERWORLD;
                case "minecraft:the_nether" -> NETHER;
                case "minecraft:the_end" -> END;
                default -> OTHER;
            };
        }
    }

    public static int level(ServerConfig cfg, Realm realm, double distance, double y, Random random) {
        double per = cfg.blocksPerLevel;
        int lvl;
        int cap;
        switch (realm) {
            case NETHER -> {
                lvl = cfg.netherBaseLevel + (int) (distance * 4 / per);
                cap = cfg.netherMaxLevel;
            }
            case END -> {
                lvl = cfg.endBaseLevel + (int) (distance / (per * 2));
                cap = cfg.endMaxLevel;
            }
            case OVERWORLD -> {
                lvl = 1 + (int) (distance / per);
                if (y < 0) {
                    lvl += 5;
                }
                cap = cfg.overworldMaxLevel;
            }
            default -> {
                lvl = 1 + (int) (distance / per);
                cap = cfg.otherDimensionMaxLevel;
            }
        }
        lvl += random.nextInt(3);
        return Math.max(1, Math.min(cap, lvl));
    }

    public static double healthMultiplier(ServerConfig cfg, int level) {
        return 1 + cfg.healthPerLevel * (level - 1);
    }

    public static double damageMultiplier(ServerConfig cfg, int level) {
        return 1 + cfg.damagePerLevel * (level - 1);
    }

    private MobLevels() {
    }
}
