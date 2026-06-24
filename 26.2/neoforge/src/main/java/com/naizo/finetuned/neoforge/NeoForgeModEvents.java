package com.naizo.finetuned.neoforge;

import com.naizo.finetuned.procedures.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class NeoForgeModEvents {
	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent.Pre event) {
		if (event.getEntity() == null || event.getSource().getEntity() == null) {
			return;
		}
		Entity entity = event.getEntity();
		Entity source = event.getSource().getEntity();
		double amount = event.getNewDamage();
		AttackGemTriggersProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, source, amount);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		Entity source = event.getSource().getEntity();
		InfernoCoreDiesProcedure.execute(entity.level(), entity, source);
		OnMobDeathProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity().level().isClientSide()) {
			return;
		}
		Player player = event.getEntity();
		MoonsLunarBloomFangPassiveProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player, player);
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		GiveWeaponTooltipProcedure.execute(event.getItemStack(), event.getToolTip());
		DisplayGemSlotsProcedure.execute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());
	}
}
