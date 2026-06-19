package com.naizo.finetuned.procedures;

import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WeaponsForgeActiveOnTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof WeaponsForgeActiveBlockEntity forge)) {
			return;
		}
		double timer = forge.getPersistentData().getDouble("forge_timer");
		if (timer > 0) {
			if (!world.isClientSide()) {
				forge.getPersistentData().putDouble("forge_timer", timer - 1);
				if (world instanceof Level level) {
					level.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
				}
			}
			if (world instanceof Level level) {
				var sound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse("minecraft:block.campfire.crackle"));
				if (!level.isClientSide()) {
					level.playSound(null, pos, sound, SoundSource.NEUTRAL, 1, 1);
				} else {
					level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 1, 1, false);
				}
			}
		} else {
			world.setBlock(pos, FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get().defaultBlockState(), 3);
		}
	}
}
