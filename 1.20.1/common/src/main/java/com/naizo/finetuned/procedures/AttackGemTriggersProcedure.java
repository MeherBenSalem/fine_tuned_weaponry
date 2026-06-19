package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public final class AttackGemTriggersProcedure {
	private AttackGemTriggersProcedure() {
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, double amount) {
		if (entity == null || sourceentity == null || !(sourceentity instanceof Player) || !(sourceentity instanceof LivingEntity living)) {
			return;
		}
		ItemStack mainHand = living.getMainHandItem();
		if (!GemNbtKeys.hasAnyAttackGem(mainHand)) {
			return;
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.INFERNO_CORE)) {
			InfernoCoreTriggerProcedure.execute(world, x, y, z, entity, sourceentity, amount);
			VolcanoBurstProcedure.execute(world, entity, sourceentity);
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.FROST_RUNE)) {
			FrostRuneTriggerProcedure.execute(world, x, y, z, entity, sourceentity, amount);
		}
		if (GemNbtKeys.hasGem(mainHand, GemNbtKeys.STORM_SHARD)) {
			StormShardTriggerProcedure.execute(world, x, y, z, entity, sourceentity, amount);
		}
	}
}
