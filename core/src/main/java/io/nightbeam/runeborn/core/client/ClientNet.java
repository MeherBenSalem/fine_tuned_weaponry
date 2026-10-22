package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.client.screen.CharacterScreen;
import io.nightbeam.runeborn.core.client.screen.OriginScreen;
import io.nightbeam.runeborn.core.client.screen.ShrineScreen;
import io.nightbeam.runeborn.core.net.Buf;
import io.nightbeam.runeborn.core.net.Packets;

/** Applies server-to-client packets. Call on the client main thread. */
public final class ClientNet {
    /** Client ticks plus partial, maintained by the version glue. */
    public static float clock;

    public static void handle(byte[] payload, ClientHost host) {
        Buf b = Buf.reader(payload);
        int id = b.readVarInt();
        switch (id) {
            case Packets.SYNC -> {
                boolean first = ClientState.view == null;
                ClientState.view = Packets.readSync(b);
                if (first) {
                    ClientState.runes = ClientState.view.data.runes;
                    ClientState.shownRunes = ClientState.runes;
                    ClientState.emberLeft = ClientState.view.data.emberLeft;
                    ClientState.azureLeft = ClientState.view.data.azureLeft;
                    ClientState.fp = (float) ClientState.view.data.focus();
                    ClientState.stamina = (float) ClientState.view.data.maxStamina();
                }
                ClientState.runes = ClientState.view.data.runes;
                ClientState.emberLeft = ClientState.view.data.emberLeft;
                ClientState.azureLeft = ClientState.view.data.azureLeft;
            }
            case Packets.VITALS -> {
                Packets.Vitals v = Packets.readVitals(b);
                ClientState.fp = v.fp();
                ClientState.stamina = v.stamina();
                ClientState.runes = v.runes();
                ClientState.emberLeft = v.emberLeft();
                ClientState.azureLeft = v.azureLeft();
                if (ClientState.view != null) {
                    ClientState.view.data.runes = v.runes();
                    ClientState.view.data.emberLeft = v.emberLeft();
                    ClientState.view.data.azureLeft = v.azureLeft();
                }
            }
            case Packets.BANNER -> {
                ClientState.banner = Packets.readBanner(b);
                ClientState.bannerTime = clock;
            }
            case Packets.TARGET -> {
                Packets.Target t = Packets.readTarget(b);
                if (ClientState.target == null || ClientState.target.entityId() != t.entityId()) {
                    ClientState.targetChip = t.maxHealth() <= 0 ? 0 : (t.health() + t.lastDamage()) / t.maxHealth();
                }
                ClientState.target = t;
                ClientState.targetTime = clock;
            }
            case Packets.BOSSES -> {
                ClientState.bosses = Packets.readBosses(b);
                ClientState.bossesTime = clock;
            }
            case Packets.OPEN -> {
                Packets.Open open = Packets.readOpen(b);
                switch (open.screen()) {
                    case SHRINE -> host.open(new ShrineScreen(open));
                    case ORIGIN -> host.open(new OriginScreen());
                    case CHARACTER -> host.open(new CharacterScreen());
                }
            }
            case Packets.RUNE_GAIN -> {
                long amount = b.readVarLong();
                if (clock - ClientState.gainTime > 60) {
                    ClientState.pendingGain = 0;
                }
                ClientState.pendingGain += amount;
                ClientState.gainTime = clock;
            }
            case Packets.FX -> {
                int k = b.readVarInt();
                float strength = b.readF32();
                Packets.Fx fx = k >= 0 && k < Packets.Fx.values().length ? Packets.Fx.values()[k] : Packets.Fx.SHAKE;
                switch (fx) {
                    case SHAKE -> {
                        ClientState.shakeUntil = clock + 8;
                        ClientState.shakeStrength = strength;
                    }
                    case FLASH_RED -> {
                        ClientState.flashTime = clock;
                        ClientState.flashColor = 0xB01818;
                    }
                    case FLASH_GOLD -> {
                        ClientState.flashTime = clock;
                        ClientState.flashColor = 0xE8C870;
                    }
                    case DODGE -> ClientState.dodgeReadyAt = clock;
                }
            }
            default -> {
                // Unknown packet from a newer server; ignore.
            }
        }
    }

    public static void tick() {
        if (ClientState.view == null) {
            return;
        }
        float target = ClientState.runes;
        float diff = target - ClientState.shownRunes;
        if (Math.abs(diff) < 1) {
            ClientState.shownRunes = target;
        } else if (clock - ClientState.gainTime > 30 || diff < 0) {
            ClientState.shownRunes += diff * 0.18f + Math.signum(diff);
        }
    }

    private ClientNet() {
    }
}
