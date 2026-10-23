package io.nightbeam.runeborn.forge;

import io.nightbeam.runeborn.compat.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ForgePlatform implements Platform.Service {
    @Override
    public String loader() {
        return "forge";
    }

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public void send(ServerPlayer player, byte[] payload) {
        ForgeNet.send(player, payload);
    }

    @Override
    public void sendToServer(byte[] payload) {
        if (FMLEnvironment.dist.isClient()) {
            RunebornForgeClient.sendToServer(payload);
        }
    }

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public Attribute entityReach() {
        return ForgeMod.ENTITY_REACH.get();
    }
}
