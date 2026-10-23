package io.nightbeam.runeborn.fabric;

import io.nightbeam.runeborn.client.RbClient;
import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.net.RbPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.DeathScreen;

public final class RunebornFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RbClient.init();
        KeyMapping.Category.register(RbClient.CATEGORY.id());
        RbClient.KEYS.forEach(KeyMappingHelper::registerKeyMapping);
        ClientPlayNetworking.registerGlobalReceiver(RbPayload.TYPE,
                (payload, context) -> context.client().execute(() -> RbClient.onPacket(payload.data())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> RbClient.onDisconnect());
        ClientTickEvents.END_CLIENT_TICK.register(client -> RbClient.tick());
        HudElementRegistry.addLast(Compat.id("hud"),
                (graphics, delta) -> RbClient.renderHud(graphics, delta.getGameTimeDeltaPartialTick(false)));
        // The Runeborn HUD draws its own health bar; hide vanilla hearts and armor while it does.
        HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR, RunebornFabricClient::unlessRunebornHud);
        HudElementRegistry.replaceElement(VanillaHudElements.ARMOR_BAR, RunebornFabricClient::unlessRunebornHud);
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> RbClient.vanillaTooltip(stack, lines));
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof DeathScreen) {
                ScreenEvents.afterExtract(screen).register((s, graphics, mouseX, mouseY, partial) ->
                        RbClient.renderDeathScreen(graphics, partial));
            }
        });
    }

    private static HudElement unlessRunebornHud(HudElement vanilla) {
        return (graphics, delta) -> {
            if (!RbClient.hideVanillaHealth()) {
                vanilla.extractRenderState(graphics, delta);
            }
        };
    }
}
