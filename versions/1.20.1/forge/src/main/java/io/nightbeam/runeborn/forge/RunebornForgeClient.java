package io.nightbeam.runeborn.forge;

import io.nightbeam.runeborn.client.RbClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client-only Forge wiring; only loaded on the physical client. */
final class RunebornForgeClient {
    static void init(IEventBus modBus) {
        ForgeNet.clientHandler = RbClient::onPacket;
        modBus.addListener((FMLClientSetupEvent e) -> RbClient.init());
        modBus.addListener((RegisterKeyMappingsEvent e) -> RbClient.KEYS.forEach(e::register));
        modBus.addListener((RegisterGuiOverlaysEvent e) -> e.registerAboveAll("hud",
                (gui, graphics, partial, width, height) -> RbClient.renderHud(graphics, partial)));

        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                RbClient.tick();
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> RbClient.onDisconnect());
        MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent e) -> RbClient.vanillaTooltip(e.getItemStack(), e.getToolTip()));
        MinecraftForge.EVENT_BUS.addListener((RenderGuiOverlayEvent.Pre e) -> {
            if ((e.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                    || e.getOverlay().id().equals(VanillaGuiOverlay.ARMOR_LEVEL.id()))
                    && RbClient.hideVanillaHealth()) {
                e.setCanceled(true);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Render.Post e) -> {
            if (e.getScreen() instanceof DeathScreen) {
                RbClient.renderDeathScreen(e.getGuiGraphics(), e.getPartialTick());
            }
        });
    }

    static void sendToServer(byte[] payload) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            ForgeNet.sendToServer(connection.getConnection(), payload);
        }
    }

    private RunebornForgeClient() {
    }
}
