package io.nightbeam.runeborn.fabric;

import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.net.RbPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;

public final class FabricPlatform implements Platform.Service {
    @Override
    public String loader() {
        return "fabric";
    }

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void send(ServerPlayer player, byte[] payload) {
        if (ServerPlayNetworking.canSend(player, RbPayload.TYPE)) {
            ServerPlayNetworking.send(player, new RbPayload(payload));
        }
    }

    @Override
    public void sendToServer(byte[] payload) {
        if (ClientPlayNetworking.canSend(RbPayload.TYPE)) {
            ClientPlayNetworking.send(new RbPayload(payload));
        }
    }

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return FabricItemGroup.builder();
    }
}
