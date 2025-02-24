package com.naizo.finetuned.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

@Mod.EventBusSubscriber
public class InfernoCoreDiesProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double particleRadius = 0;
		double particleAmount = 0;
		if (sourceentity instanceof Player) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNO_CORE.get()).toString()))) {
				if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get()).toString()))) {
					if (Mth.nextInt(RandomSource.create(), 1, 100) <= 10) {
						if (world instanceof Level _level && !_level.isClientSide())
							_level.explode(null, (entity.getX()), (entity.getY()), (entity.getZ()), 2, Level.ExplosionInteraction.MOB);
					}
				}
			}
		}
	}
}
