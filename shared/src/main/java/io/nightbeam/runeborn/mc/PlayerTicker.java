package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Per-player upkeep each tick: focus and stamina recovery, lost runes, HUD refreshes. */
public final class PlayerTicker {
    public static void tick(ServerPlayer player, long now) {
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        ServerConfig cfg = ServerConfig.INSTANCE;

        // Focus
        double maxFp = d.maxFocus();
        double fp = d.focus();
        if (fp < maxFp && player.isAlive()) {
            double regen = cfg.focusRegenPerSecond / 20.0;
            if (d.shard() == CrownShard.WYRM) {
                regen *= 2;
            }
            d.setFocus(Math.min(maxFp, fp + regen));
        }

        // Stamina
        double maxSt = d.maxStamina();
        double st = s.stamina(maxSt);
        if (st < maxSt && now >= s.staminaRegenAt) {
            double regen = cfg.staminaRegenPerSecond / 20.0;
            if (d.shard() == CrownShard.STORM) {
                regen *= 1.3;
            }
            if (player.isSprinting()) {
                regen *= 0.5;
            }
            s.stamina = Math.min(maxSt, st + regen);
        }

        if (now % 4 == 0) {
            Players.syncVitals(player, false);
        }
        if (now % 10 == 0) {
            lostRunes(player, d);
            refreshTarget(player, s, now);
        }
    }

    private static void lostRunes(ServerPlayer player, PlayerData d) {
        PlayerData.LostRunes lost = d.lost;
        if (lost == null || !lost.dimension.equals(Compat.dimensionId(player.level())) || !player.isAlive()) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();
        double dx = player.getX() - lost.x;
        double dy = player.getY() - lost.y;
        double dz = player.getZ() - lost.z;
        double dist2 = dx * dx + dy * dy + dz * dz;
        if (dist2 < 2.5 * 2.5) {
            long amount = lost.amount;
            d.lost = null;
            Players.giveRunes(player, amount);
            Players.banner(player, Packets.Banner.RUNES_RECOVERED, Lang.en(Lang.B_RECOVERED), "+" + Runes.format(amount));
            Fx.sound(player, Fx.S.RUNES_PICKUP, 1f, 1f);
            Compat.particlesFor(player, Fx.P.TOTEM, lost.x, lost.y + 0.5, lost.z, 40, 0.3, 0.5, 0.3, 0.3);
            Players.sync(player);
        } else if (dist2 < 96 * 96) {
            Compat.dustFor(player, 0x6FE8A0, 1.4f, lost.x, lost.y + 0.2, lost.z, 6, 0.25, 0.6);
            Compat.particlesFor(player, Fx.P.GLOW, lost.x, lost.y + 0.8, lost.z, 2, 0.2, 0.4, 0.2, 0.01);
        }
    }

    private static void refreshTarget(ServerPlayer player, Session s, long now) {
        if (s.lastTargetId < 0 || now - s.lastTargetAt > 80) {
            return;
        }
        Entity e = player.level().getEntity(s.lastTargetId);
        if (e instanceof LivingEntity living && e.distanceToSqr(player) < 48 * 48) {
            Players.send(player, Packets.target(Combat.targetInfo(living, living.isAlive() ? 0 : (float) s.lastHitDamage)));
            if (!living.isAlive()) {
                s.lastTargetId = -1;
            }
        }
    }

    private PlayerTicker() {
    }
}
