package com.naizo.finetuned.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

@Mod.EventBusSubscriber
public class VolcanoBurstProcedure {
	@SubscribeEvent
	public static void onPlayerCriticalHit(CriticalHitEvent event) {
		execute(event, event.getEntity().level(), event.getTarget(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double particleRadius = 0;
		double particleAmount = 0;
		if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNO_CORE.get()).toString()))) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get()).toString()))) {
				world.setBlock(BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), Blocks.LAVA.defaultBlockState(), 3);
			}
		}
	}
}
