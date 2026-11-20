package io.nightbeam.runeborn.mixin;

import io.nightbeam.runeborn.mc.Combat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Routes every hit through {@link Combat}: attribute scaling, dodge i-frames and stamina before
 * vanilla damage math, status buildup and HUD updates after it.
 *
 * <p>The new amount is written into the {@code hurt} call that is already running instead of
 * calling {@code hurt} a second time. A second call goes back through any override in a
 * subclass, so mobs with their own damage rules saw the hit twice: L_Ender's Cataclysm bosses add
 * each hit to a damage cap per hurt window, found the cap already full on the second pass and took
 * 0.1 damage, and a hit on a player went through the difficulty multiplier twice.
 *
 * <p>Both injectors sit at HEAD and Mixin does not promise which of them runs first, so whichever
 * runs first works out the amount and the other one picks it up.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private boolean runeborn$pending;
    @Unique
    private float runeborn$adjusted;
    @Unique
    private float runeborn$healthBefore;

    /** The first call for a hit works out the amount; the second returns it and clears it. */
    @Unique
    private float runeborn$take(DamageSource source, float amount) {
        if (runeborn$pending) {
            runeborn$pending = false;
            return runeborn$adjusted;
        }
        runeborn$adjusted = Combat.incoming((LivingEntity) (Object) this, source, amount);
        runeborn$pending = true;
        return runeborn$adjusted;
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void runeborn$beforeHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (runeborn$take(source, amount) < 0) {
            // Cancelled: the amount injector never runs, so nothing may stay pending.
            runeborn$pending = false;
            cir.setReturnValue(false);
            return;
        }
        runeborn$healthBefore = ((LivingEntity) (Object) this).getHealth();
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float runeborn$scaleHurt(float amount, DamageSource source) {
        float adjusted = runeborn$take(source, amount);
        return adjusted < 0 ? amount : adjusted;
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void runeborn$afterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            LivingEntity self = (LivingEntity) (Object) this;
            Combat.afterDamage(self, source, Math.max(0, runeborn$healthBefore - self.getHealth()));
        }
    }
}
