package io.nightbeam.runeborn.core.config;

/**
 * Gameplay settings, stored as {@code config/runeborn-server.json}. Field names are the JSON keys.
 * Values are synced to clients that need them for previews (level costs, merchant prices).
 */
public final class ServerConfig {
    public static ServerConfig INSTANCE = new ServerConfig();

    // Progression
    public double runeGainMultiplier = 1.0;
    public double levelCostMultiplier = 1.0;
    public double smithingCostMultiplier = 1.0;
    public double merchantPriceMultiplier = 1.0;
    public boolean dropRunesOnDeath = true;
    public boolean requireClassSelection = true;
    /**
     * Shrine level-ups need RPG Attribute System installed; set false to allow them without it.
     * With it installed, every rune level bought also grants this many RPG levels (0 to turn off).
     */
    public boolean levelUpRequiresRpgAttributeSystem = true;
    public int rpgLevelsPerRuneLevel = 1;

    // Combat
    public boolean staminaEnabled = true;
    public boolean attacksCostStamina = true;
    public double exhaustedDamageMultiplier = 0.6;
    public int dodgeStaminaCost = 18;
    public int dodgeInvulnerabilityTicks = 7;
    public double pvpDamageMultiplier = 0.5;
    public double focusRegenPerSecond = 0.5;
    public double staminaRegenPerSecond = 45;
    public boolean vanillaWeaponsScale = true;

    // World
    public boolean mobLevelsEnabled = true;
    public double blocksPerLevel = 150;
    public int overworldMaxLevel = 50;
    public int netherBaseLevel = 25;
    public int netherMaxLevel = 75;
    public int endBaseLevel = 60;
    public int endMaxLevel = 100;
    public int otherDimensionMaxLevel = 100;
    public double healthPerLevel = 0.05;
    public double damagePerLevel = 0.03;
    public double eliteChance = 0.04;
    public boolean duskRiderSpawns = true;
    public double duskRiderNightlyChance = 0.12;
    public boolean bossesBreakBlocks = false;

    // Shrines
    public boolean shrineFastTravel = true;
    public boolean shrineSetsRespawn = true;
    public int shrineTravelCooldownSeconds = 0;

    public ServerConfig sanitize() {
        runeGainMultiplier = clamp(runeGainMultiplier, 0, 1000);
        levelCostMultiplier = clamp(levelCostMultiplier, 0.01, 1000);
        smithingCostMultiplier = clamp(smithingCostMultiplier, 0, 1000);
        merchantPriceMultiplier = clamp(merchantPriceMultiplier, 0, 1000);
        exhaustedDamageMultiplier = clamp(exhaustedDamageMultiplier, 0, 1);
        rpgLevelsPerRuneLevel = (int) clamp(rpgLevelsPerRuneLevel, 0, 100);
        dodgeStaminaCost = (int) clamp(dodgeStaminaCost, 0, 1000);
        dodgeInvulnerabilityTicks = (int) clamp(dodgeInvulnerabilityTicks, 0, 40);
        pvpDamageMultiplier = clamp(pvpDamageMultiplier, 0, 10);
        blocksPerLevel = clamp(blocksPerLevel, 1, 1_000_000);
        eliteChance = clamp(eliteChance, 0, 1);
        duskRiderNightlyChance = clamp(duskRiderNightlyChance, 0, 1);
        healthPerLevel = clamp(healthPerLevel, 0, 10);
        damagePerLevel = clamp(damagePerLevel, 0, 10);
        return this;
    }

    private static double clamp(double v, double lo, double hi) {
        if (Double.isNaN(v)) {
            return lo;
        }
        return Math.max(lo, Math.min(hi, v));
    }
}
