package io.nightbeam.runeborn.fabric;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.content.RbContent;
import io.nightbeam.runeborn.mc.Hooks;
import io.nightbeam.runeborn.mc.RbCommands;
import io.nightbeam.runeborn.net.RbPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class RunebornFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RbContent.Registrar registrar = new RbContent.Registrar() {
            @Override
            public Block block(String path, Supplier<Block> factory) {
                return Registry.register(BuiltInRegistries.BLOCK, Compat.id(path), factory.get());
            }

            @Override
            public Item item(String path, Supplier<Item> factory) {
                return Registry.register(BuiltInRegistries.ITEM, Compat.id(path), factory.get());
            }

            @Override
            public CreativeModeTab tab(String path, Supplier<CreativeModeTab> factory) {
                return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Compat.id(path), factory.get());
            }
        };
        RbContent.blocks(registrar);
        RbContent.items(registrar);
        RbContent.tabs(registrar);

        PayloadTypeRegistry.clientboundPlay().register(RbPayload.TYPE, RbPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RbPayload.TYPE, RbPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RbPayload.TYPE,
                (payload, context) -> context.server().execute(() -> Hooks.packet(context.player(), payload.data())));

        ServerLifecycleEvents.SERVER_STARTED.register(Hooks::serverStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Hooks.serverStopping());
        ServerTickEvents.END_SERVER_TICK.register(Hooks::serverTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Hooks.playerJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> Hooks.playerLeave(handler.getPlayer()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> Hooks.playerRespawn(newPlayer));
        ServerEntityEvents.ENTITY_LOAD.register(Hooks::entityLoad);
        ServerLivingEntityEvents.AFTER_DEATH.register(Hooks::livingDeath);
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> RbCommands.register(dispatcher));
    }
}
