package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;

/**
 * Link to RPG Attribute System (mod id {@code rpg_attribute_system}). The two mods share
 * progression: shrine level-ups need it installed, and every rune level bought there also grants
 * RPG levels through its own level-up pipeline, so their points are spent in its screen.
 * There is no compile-time dependency; the link goes through its documented commands.
 */
public final class RasBridge {
    public static final String MOD_ID = "rpg_attribute_system";
    private static Boolean installed;

    public static boolean installed() {
        if (installed == null) {
            installed = Platform.isModLoaded(MOD_ID);
            if (installed) {
                Runeborn.LOG.info("RPG Attribute System found: shrine levels also grant RPG levels");
            }
        }
        return installed;
    }

    /** Whether players can buy rune levels at shrines. */
    public static boolean levelingEnabled() {
        return installed() || !ServerConfig.INSTANCE.levelUpRequiresRpgAttributeSystem;
    }

    /** RPG levels granted per rune level, 0 when the link is inactive. */
    public static int rpgLevelsPerRuneLevel() {
        return installed() ? ServerConfig.INSTANCE.rpgLevelsPerRuneLevel : 0;
    }

    /** Grants the RPG levels earned by buying {@code runeLevels} rune levels. */
    public static void onRuneLevels(ServerPlayer player, int runeLevels) {
        int levels = runeLevels * rpgLevelsPerRuneLevel();
        if (levels <= 0) {
            return;
        }
        // "/ras add level" runs RAS's full level-up pipeline: points, rewards and its banner. Its
        // player argument rejects UUIDs (they could name any entity), so pass the player's name.
        String name = player.getScoreboardName();
        if (!Compat.runCommand(player, "ras add level " + name + " " + levels)) {
            Runeborn.LOG.warn("Could not grant {} RPG levels to {}", levels, name);
        }
    }

    private RasBridge() {
    }
}
