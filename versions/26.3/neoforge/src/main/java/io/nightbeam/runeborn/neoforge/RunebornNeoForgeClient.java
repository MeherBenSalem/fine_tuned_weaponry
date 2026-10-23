package io.nightbeam.runeborn.neoforge;

import io.nightbeam.runeborn.client.RbClient;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.net.RbPayload;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Client-only NeoForge wiring; only loaded on the physical client. */
final class RunebornNeoForgeClient {
    static void init(IEventBus modBus) {
        modBus.addListener((FMLClientSetupEvent e) -> RbClient.init());
        modBus.addListener((RegisterKeyMappingsEvent e) -> {
            e.registerCategory(RbClient.CATEGORY);
            RbClient.KEYS.forEach(e::register);
        });
        modBus.addListener((RegisterClientPayloadHandlersEvent e) ->
                e.register(RbPayload.TYPE, (payload, context) -> RbClient.onPacket(payload.data())));
        modBus.addListener((RegisterGuiLayersEvent e) -> e.registerAboveAll(Compat.id("hud"),
                (graphics, delta) -> RbClient.renderHud(graphics, delta.getGameTimeDeltaPartialTick(false))));

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> RbClient.tick());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> RbClient.onDisconnect());
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent e) -> RbClient.vanillaTooltip(e.getItemStack(), e.getToolTip()));
        NeoForge.EVENT_BUS.addListener((RenderGuiLayerEvent.Pre e) -> {
            if ((e.getName().equals(VanillaGuiLayers.PLAYER_HEALTH) || e.getName().equals(VanillaGuiLayers.ARMOR_LEVEL))
                    && RbClient.hideVanillaHealth()) {
                e.setCanceled(true);
            }
        });
        NeoForge.EVENT_BUS.addListener((ScreenEvent.Render.Post e) -> {
            if (e.getScreen() instanceof DeathScreen) {
                RbClient.renderDeathScreen(e.getGuiGraphics(), e.getPartialTick());
            }
        });
    }

    static void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }

    private RunebornNeoForgeClient() {
    }
}
