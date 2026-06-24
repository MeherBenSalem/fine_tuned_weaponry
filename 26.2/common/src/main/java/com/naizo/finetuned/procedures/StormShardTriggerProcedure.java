package com.naizo.finetuned.procedures;

import com.naizo.finetuned.init.FineTunedWeaponryModEnchantments;
import com.naizo.finetuned.util.EnchantmentApplication;
import com.naizo.finetuned.util.GemNbtKeys;
import com.naizo.finetuned.util.ItemStackDataHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class StormShardTriggerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player) || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (!GemNbtKeys.hasGem(mainHand, GemNbtKeys.STORM_SHARD)) {
			return;
		}
		int chance = GemNbtKeys.hasGem(mainHand, GemNbtKeys.CHARGED_AMPLIFIER) ? 20 : 10;
		if (Mth.nextInt(world.getRandom(), 1, 100) <= chance) {
			if (world instanceof ServerLevel level) {
				LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
				if (bolt != null) {
					bolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ())));
					level.addFreshEntity(bolt);
				}
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.OVERLOAD_AMPLIFIER) && entity instanceof LivingEntity target && !target.level().isClientSide()) {
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
				target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.STATIC_AMPLIFIER) && !living.level().isClientSide()) {
				living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
				living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 0));
			}
			if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.SUPERSTORM_AMPLIFIER)) {
				final Vec3 center = new Vec3(x, y, z);
				List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(3), e -> e != sourceentity);
				for (LivingEntity target : nearby) {
					if (world instanceof ServerLevel level) {
						LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
						if (bolt != null) {
							bolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(target.getX(), target.getY(), target.getZ())));
							level.addFreshEntity(bolt);
						}
					}
				}
			}
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.STORM_FURY_AMPLIFIER)) {
			CompoundTagHolder chargeHolder = new CompoundTagHolder();
			ItemStackDataHelper.updateTag(mainHand, tag -> {
				double charge = tag.getDouble("stormcharge") + 1;
				tag.putDouble("stormcharge", charge);
				chargeHolder.value = charge;
			});
			double charge = chargeHolder.value;
			Holder<Enchantment> superCharge = EnchantmentApplication.holder(world, FineTunedWeaponryModEnchantments.SUPER_CHARGE_ENCHANT.id());
			if (charge == 5) {
				EnchantmentHelper.updateEnchantments(mainHand, builder -> builder.set(superCharge, 10));
			}
			if (charge == 6) {
				ItemStackDataHelper.updateTag(mainHand, tag -> tag.putDouble("stormcharge", 0));
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LIGHTNING_BOLT)), (float) (amount * 2));
				EnchantmentApplication.remove(world, mainHand, ResourceKey.create(Registries.ENCHANTMENT, FineTunedWeaponryModEnchantments.SUPER_CHARGE_ENCHANT.id()));
			}
		}
	}

	private static final class CompoundTagHolder {
		private double value;
	}
}
