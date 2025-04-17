package com.naizo.finetuned.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Comparator;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

@Mod.EventBusSubscriber
public class InfernoCoreTriggerProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity(), event.getSource().getEntity(), event.getAmount());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		execute(null, world, x, y, z, entity, sourceentity, amount);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		if (entity == null || sourceentity == null)
			return;
		double particleRadius = 0;
		double particleAmount = 0;
		if (sourceentity instanceof Player) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNO_CORE.get()).toString()))) {
				if (Mth.nextInt(RandomSource.create(), 1, 100) <= 20) {
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get()).toString()))) {
						entity.setSecondsOnFire(5);
					} else {
						entity.setSecondsOnFire(3);
					}
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get()).toString()))) {
						{
							final Vec3 _center = new Vec3(x, y, z);
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (!(entityiterator == sourceentity) && entityiterator instanceof LivingEntity) {
									entityiterator.setSecondsOnFire(3);
								}
							}
						}
					}
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get()).toString()))) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1));
					}
					if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get()).toString()))) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 1));
					}
				}
				if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get()).toString()))) {
					if (entity.getRemainingFireTicks() >= 20) {
						entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), (float) (amount * 0.2));
					}
				}
				if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get()).toString()))) {
					if (sourceentity instanceof LivingEntity _entity)
						_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + amount + 0.2));
				}
			}
		}
	}
}
