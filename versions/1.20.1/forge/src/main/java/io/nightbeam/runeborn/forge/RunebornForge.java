package io.nightbeam.runeborn.forge;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.content.RbContent;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.mc.Hooks;
import io.nightbeam.runeborn.mc.RbCommands;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

@Mod(Runeborn.MOD_ID)
public final class RunebornForge {
    public RunebornForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(RunebornForge::register);
        ForgeNet.init();

        MinecraftForge.EVENT_BUS.addListener((ServerStartedEvent e) -> Hooks.serverStarted(e.getServer()));
        MinecraftForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> Hooks.serverStopping());
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                Hooks.serverTick(e.getServer());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerJoin(p);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerLeave(p);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent e) -> {
            if (e.getEntity() instanceof ServerPlayer p) {
                Hooks.playerRespawn(p);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((EntityJoinLevelEvent e) -> {
            if (e.getLevel() instanceof ServerLevel level) {
                Hooks.entityLoad(e.getEntity(), level);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((LivingDeathEvent e) -> {
            if (!e.getEntity().level().isClientSide) {
                Hooks.livingDeath(e.getEntity(), e.getSource());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> RbCommands.register(e.getDispatcher()));

        if (FMLEnvironment.dist.isClient()) {
            RunebornForgeClient.init(modBus);
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

            @Override
            public Item weapon(WeaponSpec spec, Item.Properties properties) {
                return new ForgeWeaponItem(spec, properties);
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
}
