package com.naizo.finetuned.procedures;

import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.text.DecimalFormat;

public class WeaponForgeReturnTimeProcedure {
	public static String execute(LevelAccessor world, double x, double y, double z) {
		BlockEntity blockEntity = world.getBlockEntity(BlockPos.containing(x, y, z));
		if (blockEntity instanceof WeaponsForgeActiveBlockEntity forge) {
			return new DecimalFormat("##").format(forge.getPersistentData().getDouble("forge_timer")) + "s";
		}
		return "0s";
	}
}
