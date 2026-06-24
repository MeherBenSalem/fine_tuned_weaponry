package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;

import java.util.List;

public class FrostRuneTriggerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player) || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (!GemNbtKeys.hasGem(mainHand, GemNbtKeys.FROST_RUNE)) {
			return;
		}
		double freezer = GemNbtKeys.hasGem(mainHand, GemNbtKeys.GLACIAL_AMPLIFIER) ? 40 : 20;
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.PERMAFROST_AMPLIFIER) && entity.getTicksFrozen() >= 100) {
			entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FREEZE)), (float) amount);
		}
		entity.setTicksFrozen((int) (entity.getTicksFrozen() + freezer));
		if (entity instanceof LivingEntity target && !target.level().isClientSide()) {
			target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
			target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.BLIZZARD_AMPLIFIER)) {
			final Vec3 center = new Vec3(x, y, z);
			List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(5), e -> e != sourceentity);
			for (LivingEntity target : nearby) {
				if (!target.level().isClientSide()) {
					target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
					target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
				}
			}
		}
	}
}
