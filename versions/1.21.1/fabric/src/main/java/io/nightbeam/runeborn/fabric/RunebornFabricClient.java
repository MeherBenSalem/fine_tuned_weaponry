package io.nightbeam.runeborn.fabric;

import io.nightbeam.runeborn.client.RbClient;
import io.nightbeam.runeborn.net.RbPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.DeathScreen;

public final class RunebornFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RbClient.init();
        RbClient.KEYS.forEach(KeyBindingHelper::registerKeyBinding);
        ClientPlayNetworking.registerGlobalReceiver(RbPayload.TYPE,
                (payload, context) -> context.client().execute(() -> RbClient.onPacket(payload.data())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> RbClient.onDisconnect());
        ClientTickEvents.END_CLIENT_TICK.register(client -> RbClient.tick());
        HudRenderCallback.EVENT.register((graphics, delta) -> RbClient.renderHud(graphics, delta.getGameTimeDeltaPartialTick(false)));
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> RbClient.vanillaTooltip(stack, lines));
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof DeathScreen) {
                ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, partial) ->
                        RbClient.renderDeathScreen(graphics, partial));
            }
        });
    }
}
