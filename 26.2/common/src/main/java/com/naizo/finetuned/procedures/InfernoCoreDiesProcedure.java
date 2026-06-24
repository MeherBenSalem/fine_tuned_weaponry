package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class InfernoCoreDiesProcedure {
	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player) || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.INFERNO_CORE) && GemNbtKeys.hasGem(mainHand, GemNbtKeys.PYROCLASM_AMPLIFIER)) {
			if (Mth.nextInt(world.getRandom(), 1, 100) <= 10 && world instanceof Level level && !level.isClientSide()) {
				level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 2, Level.ExplosionInteraction.MOB);
			}
		}
	}
}
