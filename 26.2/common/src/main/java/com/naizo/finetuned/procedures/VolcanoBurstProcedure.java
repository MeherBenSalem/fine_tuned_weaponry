package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class VolcanoBurstProcedure {
	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.INFERNO_CORE) && GemNbtKeys.hasGem(mainHand, GemNbtKeys.VOLCANIC_BURST_AMPLIFIER)) {
			world.setBlock(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), Blocks.LAVA.defaultBlockState(), 3);
		}
	}
}
