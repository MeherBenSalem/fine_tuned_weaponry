package io.nightbeam.runeborn.fabric.mixin;

import io.nightbeam.runeborn.content.WeaponItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hides the vanilla attribute lines on Runeborn weapons (Forge reads the item's default hide flags itself). */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getHideFlags", at = @At("RETURN"), cancellable = true)
    private void runeborn$weaponHideFlags(CallbackInfoReturnable<Integer> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof WeaponItem weapon) {
            cir.setReturnValue(cir.getReturnValueI() | weapon.getDefaultTooltipHideFlags(self));
        }
    }
}
