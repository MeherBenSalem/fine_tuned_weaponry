package io.nightbeam.runeborn.forge;

import io.nightbeam.runeborn.mc.Hooks;
import io.nightbeam.runeborn.net.RbChannel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.function.Consumer;

/**
 * The {@code runeborn:main} channel on Forge. An event channel carries the raw byte array, so the wire
 * format matches Fabric. Either side may lack the channel (vanilla or Runeborn-less peers).
 */
final class ForgeNet {
    private static final String VERSION = "1";
    static final EventNetworkChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(RbChannel.ID)
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(NetworkRegistry.acceptMissingOr(VERSION))
            .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(VERSION))
            .eventNetworkChannel();

    /** Set by the client entrypoint; null on dedicated servers. */
    static Consumer<byte[]> clientHandler;

    static void init() {
        CHANNEL.addListener(ForgeNet::receive);
    }

    private static void receive(NetworkEvent event) {
        NetworkEvent.Context ctx = event.getSource().get();
        NetworkDirection direction = ctx.getDirection();
        if (event.getPayload() == null
                || (direction != NetworkDirection.PLAY_TO_SERVER && direction != NetworkDirection.PLAY_TO_CLIENT)) {
            return;
        }
        byte[] data = RbChannel.decode(event.getPayload());
        if (direction == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ctx.enqueueWork(() -> Hooks.packet(player, data));
            }
        } else if (clientHandler != null) {
            ctx.enqueueWork(() -> clientHandler.accept(data));
        }
        ctx.setPacketHandled(true);
    }

    static void send(ServerPlayer player, byte[] payload) {
        if (CHANNEL.isRemotePresent(player.connection.connection)) {
            player.connection.send(new ClientboundCustomPayloadPacket(RbChannel.ID, RbChannel.encode(payload)));
        }
    }

    static void sendToServer(Connection connection, byte[] payload) {
        if (CHANNEL.isRemotePresent(connection)) {
            connection.send(new ServerboundCustomPayloadPacket(RbChannel.ID, RbChannel.encode(payload)));
        }
    }

    private ForgeNet() {
    }
}
