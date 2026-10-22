package io.nightbeam.runeborn.neoforge;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.content.RbContent;
import io.nightbeam.runeborn.mc.Hooks;
import io.nightbeam.runeborn.mc.RbCommands;
import io.nightbeam.runeborn.net.RbPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Supplier;

@Mod(Runeborn.MOD_ID)
public final class RunebornNeoForge {
    public RunebornNeoForge(IEventBus modBus, ModContainer container) {
        modBus.addListener(RunebornNeoForge::register);
        modBus.addListener(RunebornNeoForge::payloads);

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> Hooks.serverStarted(e.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> Hooks.serverStopping());
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e) -> Hooks.serverTick(e.getServer()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerJoin(p);
            }
        });
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerLeave(p);
            }
        });
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerRespawn(p);
            }
        });
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getLevel() instanceof ServerLevel level) {
                Hooks.entityLoad(e.getEntity(), level);
            }
        });
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent e) -> {
            if (!e.getEntity().level().isClientSide) {
                Hooks.livingDeath(e.getEntity(), e.getSource());
            }
        });
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> RbCommands.register(e.getDispatcher()));

        if (FMLEnvironment.dist.isClient()) {
            RunebornNeoForgeClient.init(modBus);
        }
    }

    private static void register(RegisterEvent event) {
        RbContent.Registrar registrar = new RbContent.Registrar() {
            @Override
            public Block block(String path, Supplier<Block> factory) {
                Block block = factory.get();
                event.register(Registries.BLOCK, Compat.id(path), () -> block);
                return block;
            }

            @Override
            public Item item(String path, Supplier<Item> factory) {
                Item item = factory.get();
                event.register(Registries.ITEM, Compat.id(path), () -> item);
                return item;
            }

            @Override
            public CreativeModeTab tab(String path, Supplier<CreativeModeTab> factory) {
                CreativeModeTab tab = factory.get();
                event.register(Registries.CREATIVE_MODE_TAB, Compat.id(path), () -> tab);
                return tab;
            }
        };
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            RbContent.blocks(registrar);
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            RbContent.items(registrar);
        } else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            RbContent.tabs(registrar);
        }
    }

    private static void payloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional().playBidirectional(RbPayload.TYPE, RbPayload.CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                Hooks.packet(player, payload.data());
            } else {
                RunebornNeoForgeClient.packet(payload.data());
            }
        });
    }
}
