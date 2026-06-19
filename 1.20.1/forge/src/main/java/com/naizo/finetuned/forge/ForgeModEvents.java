package com.naizo.finetuned.forge;

import com.naizo.finetuned.procedures.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeModEvents {
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		if (event.getEntity() == null || event.getSource().getEntity() == null) {
			return;
		}
		Entity entity = event.getEntity();
		Entity source = event.getSource().getEntity();
		double amount = event.getAmount();
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
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
			return;
		}
		Player player = event.player;
		MoonsLunarBloomFangPassiveProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player, player);
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		GiveWeaponTooltipProcedure.execute(event.getItemStack(), event.getToolTip());
		DisplayGemSlotsProcedure.execute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());
	}
}
