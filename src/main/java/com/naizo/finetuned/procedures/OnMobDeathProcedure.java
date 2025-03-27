package com.naizo.finetuned.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

@Mod.EventBusSubscriber
public class OnMobDeathProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Blaze) {
			if (Mth.nextInt(RandomSource.create(), 0, 100) <= 10) {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(FineTunedWeaponryModItems.INFERNO_CORE.get()));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
			}
		}
		if (entity instanceof Illusioner) {
			if (Mth.nextInt(RandomSource.create(), 0, 100) <= 10) {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(FineTunedWeaponryModItems.STORM_SHARD.get()));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
			}
		}
		if (entity instanceof PolarBear) {
			if (Mth.nextInt(RandomSource.create(), 0, 100) <= 10) {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(FineTunedWeaponryModItems.FROST_RUNE.get()));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
			}
		}
	}
}
