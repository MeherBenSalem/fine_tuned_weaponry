package com.naizo.finetuned.procedures;

import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tn.naizo.jauml.JaumlConfigLib;

public class WeaponsForgeActiveBlockAddedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity instanceof WeaponsForgeActiveBlockEntity forge) {
				forge.getPersistentData().putDouble("forge_timer", JaumlConfigLib.getNumberValue("fine_tuned_weaponry", "main_config", "forge_timer"));
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}
}
