package io.nightbeam.runeborn.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.nightbeam.runeborn.content.WeaponItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

/** No vanilla sweep for Runeborn weapons: shared combat sweeps by weapon class instead. */
@Mixin(Player.class)
public abstract class PlayerMixin {
    @WrapOperation(method = "attack", constant = @Constant(classValue = SwordItem.class))
    private boolean runeborn$noVanillaSweep(Object item, Operation<Boolean> original) {
        return !(item instanceof WeaponItem) && original.call(item);
    }
}
