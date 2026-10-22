package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.net.Buf;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.stat.Attr;
import net.minecraft.server.level.ServerPlayer;

/** Client-to-server packets. Always called on the server thread. */
public final class ServerNet {
    public static void handle(ServerPlayer player, byte[] payload) {
        if (!RbServer.running()) {
            return;
        }
        try {
            Buf b = Buf.reader(payload);
            int id = b.readVarInt();
            switch (id) {
                case Packets.CHOOSE_ORIGIN -> Progression.chooseOrigin(player, b.readStr());
                case Packets.LEVEL_UP -> {
                    if (Shrines.near(player)) {
                        Progression.levelUp(player, b.readInts(Attr.COUNT));
                    }
                }
                case Packets.SHRINE -> {
                    int a = b.readVarInt();
                    Packets.ShrineAction action = a >= 0 && a < Packets.ShrineAction.values().length
                            ? Packets.ShrineAction.values()[a] : null;
                    int x = b.readVarInt();
                    int y = b.readVarInt();
                    String s = b.readStr();
                    int[] attrs = action == Packets.ShrineAction.REBIRTH ? b.readInts(Attr.COUNT) : null;
                    if (action != null) {
                        Shrines.action(player, action, x, y, s, attrs);
                    }
                }
                case Packets.DODGE -> dodge(player);
                case Packets.OPEN_CHARACTER -> {
                    PlayerData d = RbServer.data(player);
                    Players.sync(player);
                    Players.send(player, Packets.open(d.hasOrigin() ? Packets.Screen.CHARACTER : Packets.Screen.ORIGIN,
                            "", "", 0, 0, 0));
                }
                case Packets.USE_ART -> Arts.use(player, player.getMainHandItem());
                default -> {
                }
            }
        } catch (RuntimeException e) {
            Runeborn.LOG.warn("Bad Runeborn packet from {}: {}", player.getName().getString(), e.toString());
        }
    }

    private static void dodge(ServerPlayer player) {
        ServerConfig cfg = ServerConfig.INSTANCE;
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        long now = RbServer.now();
        if (now - s.lastDodgeAt < 12 || !player.isAlive()) {
            return;
        }
        if (cfg.staminaEnabled && !player.isCreative()) {
            double st = s.stamina(d.maxStamina());
            if (st < cfg.dodgeStaminaCost) {
                return;
            }
            s.stamina = st - cfg.dodgeStaminaCost;
            s.staminaRegenAt = now + 18;
        }
        s.lastDodgeAt = now;
        s.invulnerableUntil = now + cfg.dodgeInvulnerabilityTicks;
        player.resetFallDistance();
        Fx.sound(player, Fx.S.DODGE, 0.7f, 1f + (float) Math.random() * 0.2f);
        Players.syncVitals(player, true);
    }

    private ServerNet() {
    }
}
