package io.nightbeam.runeborn.mixin;

import io.nightbeam.runeborn.mc.Combat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Routes every hit through {@link Combat}: attribute scaling, dodge i-frames and stamina before
 * vanilla damage math, status buildup and HUD updates after it.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private boolean runeborn$adjusted;
    @Unique
    private float runeborn$healthBefore;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void runeborn$beforeHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (runeborn$adjusted) {
            runeborn$healthBefore = self.getHealth();
            return;
        }
        float adjusted = Combat.incoming(self, source, amount);
        if (adjusted < 0) {
            cir.setReturnValue(false);
            return;
        }
        if (adjusted != amount) {
            runeborn$adjusted = true;
            try {
                cir.setReturnValue(self.hurt(source, adjusted));
            } finally {
                runeborn$adjusted = false;
            }
            return;
        }
        runeborn$healthBefore = self.getHealth();
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void runeborn$afterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            LivingEntity self = (LivingEntity) (Object) this;
            Combat.afterDamage(self, source, Math.max(0, runeborn$healthBefore - self.getHealth()));
        }
    }
}
