package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tn.naizo.jauml.JaumlConfigLib;

import java.util.List;

public class RosegoldhammerRightclickedProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null || !(entity instanceof LivingEntity source)) {
			return;
		}
		if (entity instanceof Player player && player.getCooldowns().isOnCooldown(itemstack.getItem())) {
			return;
		}
		double range = JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "rose_hammer_heal_range");
		int regenDuration = (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "rose_hammer_regen_duration");
		int regenLevel = (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "rose_hammer_regen_level");
		float healAmount = (float) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "rose_hammer_heal_amount");
		Vec3 center = entity.position();
		List<LivingEntity> allies = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(range),
				e -> e == entity || (e instanceof Player) || (e instanceof LivingEntity living && living.isAlliedTo(source)));
		for (LivingEntity ally : allies) {
			ally.heal(healAmount);
			ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, regenLevel));
			world.addParticle(ParticleTypes.HEART, ally.getX(), ally.getY() + 1.2, ally.getZ(), 0, 0, 0);
		}
		if (entity instanceof Player player) {
			player.getCooldowns().addCooldown(itemstack.getItem(), (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "rose_hammer_cooldown"));
		}
	}
}
