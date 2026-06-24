package com.naizo.finetuned.procedures;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.util.Mth;
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

import java.util.Comparator;
import java.util.List;

public class InfernoCoreTriggerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player) || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (!GemNbtKeys.hasGem(mainHand, GemNbtKeys.INFERNO_CORE)) {
			return;
		}
		if (Mth.nextInt(world.getRandom(), 1, 100) <= 20) {
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.BLAZING_AMPLIFIER)) {
				entity.igniteForSeconds(5);
			} else {
				entity.igniteForSeconds(3);
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.WILDFIRE_AMPLIFIER)) {
				final Vec3 center = new Vec3(x, y, z);
				List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(2.5), e -> e != sourceentity);
				for (LivingEntity target : nearby) {
					target.igniteForSeconds(3);
				}
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.LAVA_INFUSION_AMPLIFIER) && entity instanceof LivingEntity target && !target.level().isClientSide()) {
				target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1));
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.SMOKESCREEN_AMPLIFIER) && entity instanceof LivingEntity target && !target.level().isClientSide()) {
				target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 1));
			}
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.HELLFIRE_AMPLIFIER) && entity.getRemainingFireTicks() >= 20) {
			entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), (float) (amount * 0.2));
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.INFERNAL_HUNGER_AMPLIFIER) && sourceentity instanceof LivingEntity healer) {
			healer.setHealth((float) ((entity instanceof LivingEntity victim ? victim.getHealth() : -1) + amount + 0.2));
		}
	}
}
