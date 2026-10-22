package io.nightbeam.runeborn.neoforge;

import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.net.RbPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public final class NeoForgePlatform implements Platform.Service {
    @Override
    public String loader() {
        return "neoforge";
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.getDist().isClient();
    }

    @Override
    public void send(ServerPlayer player, byte[] payload) {
        if (player.connection.hasChannel(RbPayload.TYPE)) {
            PacketDistributor.sendToPlayer(player, new RbPayload(payload));
        }
    }

    @Override
    public void sendToServer(byte[] payload) {
        RunebornNeoForgeClient.sendToServer(new RbPayload(payload));
    }

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return CreativeModeTab.builder();
    }
}
