package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.ModConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import tn.naizo.jauml.JaumlConfigLib;

public class HollowManStaffRightclickedProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null || !(entity instanceof LivingEntity living)) {
			return;
		}
		if (entity instanceof Player player && player.getCooldowns().isOnCooldown(itemstack.getItem())) {
			return;
		}
		int duration = (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "hollow_staff_haste_duration");
		int level = (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "hollow_staff_haste_level");
		living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, level));
		if (entity instanceof Player player) {
			player.getCooldowns().addCooldown(itemstack.getItem(), (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "hollow_staff_cooldown"));
		}
	}
}
