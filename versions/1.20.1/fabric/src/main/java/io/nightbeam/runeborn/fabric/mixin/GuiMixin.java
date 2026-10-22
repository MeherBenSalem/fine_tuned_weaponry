package io.nightbeam.runeborn.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.nightbeam.runeborn.client.RbClient;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides vanilla hearts and armor icons; the Runeborn HUD draws its own health bar. On 1.20.1 the armor
 * row is drawn inline in {@code renderPlayerHealth}, so it is hidden by reading the armor value as 0
 * there, which leaves food and air untouched.
 */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void runeborn$hideHearts(CallbackInfo ci) {
        if (RbClient.hideVanillaHealth()) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "renderPlayerHealth",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
    private int runeborn$hideArmor(int armor) {
        return RbClient.hideVanillaHealth() ? 0 : armor;
    }
}
