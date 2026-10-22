package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Entry points called by each loader's event glue. Keeps loader code to a few lines per event. */
public final class Hooks {
    public static void serverStarted(MinecraftServer server) {
        RbServer.start(server);
        SelfTest.maybeRun(server);
    }

    public static void serverStopping() {
        RbServer.stop();
    }

    public static void serverTick(MinecraftServer server) {
        if (!RbServer.running()) {
            return;
        }
        RbServer.tick();
        long now = RbServer.now();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerTicker.tick(player, now);
        }
        if (now % 20 == 0) {
            Status.tick(now);
        }
        if (now % 10 == 0) {
            Mobs.tickAuras();
        }
        BossManager.tick(server, now);
    }

    public static void playerJoin(ServerPlayer player) {
        if (!RbServer.running()) {
            return;
        }
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        s.stamina = d.maxStamina();
        Players.applyAttributes(player);
        Players.sync(player);
        if (!d.hasOrigin() && ServerConfig.INSTANCE.requireClassSelection) {
            RbServer.later(30, () -> {
                if (!player.isRemoved() && !RbServer.data(player).hasOrigin()) {
                    Players.send(player, Packets.open(Packets.Screen.ORIGIN, "", "", 0, 0, 0));
                }
            });
        }
    }

    public static void playerLeave(ServerPlayer player) {
        if (RbServer.running()) {
            RbServer.unload(player);
        }
    }

    public static void playerRespawn(ServerPlayer player) {
        if (!RbServer.running()) {
            return;
        }
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        s.stamina = d.maxStamina();
        s.invulnerableUntil = 0;
        d.setFocus(d.maxFocus());
        Players.applyAttributes(player);
        player.setHealth(player.getMaxHealth());
        Players.sync(player);
    }

    public static void entityLoad(Entity entity, ServerLevel level) {
        if (!RbServer.running()) {
            return;
        }
        BossManager.onLoad(entity);
        Mobs.onLoad(entity, level);
    }

    public static void livingDeath(LivingEntity entity, DamageSource source) {
        Mobs.onDeath(entity, source);
    }

    public static void packet(ServerPlayer player, byte[] payload) {
        ServerNet.handle(player, payload);
    }

    private Hooks() {
    }
}
