package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;


import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import com.naizo.finetuned.util.RegistryHelper;

public class WeaponsForgeUnActiveOnBlockRightClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == RegistryHelper.getItem(
				(JaumlConfigLib.getStringValue("fine_tuned_weaponry", "main_config", "forge_ignite_item")).toLowerCase(java.util.Locale.ENGLISH))) {
			world.setBlock(BlockPos.containing(x, y, z), FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get().defaultBlockState(), 3);
			if (entity instanceof Player _player) {
				ItemStack _stktoremove = new ItemStack(RegistryHelper.getItem((JaumlConfigLib.getStringValue("fine_tuned_weaponry", "main_config", "forge_ignite_item")).toLowerCase(java.util.Locale.ENGLISH)));
				_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), RegistryHelper.getSoundEvent("entity.blaze.shoot"), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, RegistryHelper.getSoundEvent("entity.blaze.shoot"), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("the forge needs a energy source to function properly"), false);
		}
	}
}
