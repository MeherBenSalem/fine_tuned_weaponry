package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.feat.Feat;
import io.nightbeam.runeborn.core.item.Recipes;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/** Grants the Runeborn advancement tab ("feats") as milestones happen. */
public final class Feats {
    public static void grant(ServerPlayer player, String feat) {
        Feat f = Feat.get(feat);
        if (f != null) {
            Compat.grantAdvancement(player, f.path());
        }
    }

    public static void levels(ServerPlayer player, PlayerData d) {
        for (int i = 0; i < Feat.LEVEL_THRESHOLDS.length; i++) {
            if (d.level() >= Feat.LEVEL_THRESHOLDS[i]) {
                grant(player, Feat.LEVEL_FEATS[i]);
            }
        }
    }

    public static void reinforced(ServerPlayer player, WeaponSpec spec, int level) {
        int tempered = spec.path == WeaponSpec.Path.STANDARD ? 10 : 4;
        if (level >= tempered) {
            grant(player, Feat.TEMPERED);
        }
        if (level >= spec.path.max) {
            grant(player, Feat.MASTERWORK);
        }
    }

    public static void felled(ServerPlayer player, BossSpec boss) {
        if (boss.summonable()) {
            grant(player, Feat.fell(boss));
        }
    }

    public static void shards(ServerPlayer player, PlayerData d) {
        if (d.shards.size() >= CrownShard.values().length) {
            grant(player, Feat.CROWN);
        }
    }

    /** On join: shows Runeborn recipes in the recipe book and backfills feats earned before they existed. */
    public static void join(ServerPlayer player) {
        List<String> recipes = new ArrayList<>();
        for (Recipes.Recipe r : Recipes.all()) {
            recipes.add(r.id());
        }
        Compat.unlockRecipes(player, recipes);
        PlayerData d = RbServer.data(player);
        if (!d.hasOrigin()) {
            return;
        }
        grant(player, Feat.ROOT);
        levels(player, d);
        if (!d.shrines.isEmpty()) {
            grant(player, Feat.KINDLE);
        }
        if (d.champions > 0) {
            grant(player, Feat.CHAMPION);
        }
        if (d.deaths > 0) {
            grant(player, Feat.DIED);
        }
        for (BossSpec boss : Bosses.all()) {
            if (d.hasFelled(boss.id())) {
                felled(player, boss);
            }
        }
        shards(player, d);
    }

    private Feats() {
    }
}
