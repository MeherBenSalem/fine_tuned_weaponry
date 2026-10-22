package io.nightbeam.runeborn.fabric.mixin;

import io.nightbeam.runeborn.client.RbClient;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hides vanilla hearts and armor icons; the Runeborn HUD draws its own health bar. */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void runeborn$hideHearts(CallbackInfo ci) {
        if (RbClient.hideVanillaHealth()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void runeborn$hideArmor(CallbackInfo ci) {
        if (RbClient.hideVanillaHealth()) {
            ci.cancel();
        }
    }
}
