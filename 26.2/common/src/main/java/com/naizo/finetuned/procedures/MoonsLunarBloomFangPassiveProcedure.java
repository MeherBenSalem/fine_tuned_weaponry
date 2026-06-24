package com.naizo.finetuned.procedures;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MoonsLunarBloomFangPassiveProcedure {
	private static final int TICK_INTERVAL = 20;

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null) {
			return;
		}
		if (entity.tickCount % TICK_INTERVAL != 0) {
			return;
		}
		ItemStack mainHand = sourceentity instanceof LivingEntity living ? living.getMainHandItem() : ItemStack.EMPTY;
		if (mainHand.getItem() != FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG.get()) {
			return;
		}
		BlockPos below = BlockPos.containing(entity.getX(), entity.getY() - 1, entity.getZ());
		BlockState belowState = world.getBlockState(below);
		if (belowState.is(Blocks.DIRT) || belowState.is(Blocks.COARSE_DIRT)) {
			world.setBlock(below, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
		}
		if (world instanceof Level level) {
			ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
			if (BoneMealItem.growCrop(boneMeal, level, below) || BoneMealItem.growWaterPlant(boneMeal, level, below, null)) {
				if (!level.isClientSide()) {
					level.levelEvent(2005, below, 0);
				}
			}
		}
	}
}
