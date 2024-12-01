package com.naizo.finetuned.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber
public class AddToolTipEventProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		execute(event, event.getItemStack(), event.getToolTip());
	}

	public static void execute(ItemStack itemstack, List<Component> tooltip) {
		execute(null, itemstack, tooltip);
	}

	private static void execute(@Nullable Event event, ItemStack itemstack, List<Component> tooltip) {
		if (tooltip == null)
			return;
		double renderSlot = 0;
		if (itemstack.getOrCreateTag().getBoolean("ftw_en")) {
			renderSlot = 1;
			if (Screen.hasShiftDown()) {
				tooltip.add((int) renderSlot, Component.literal("\u00A76Ascensions"));
				renderSlot = renderSlot + 1;
				if (itemstack.getOrCreateTag().getDouble("ftwp_gminer") > 0) {
					tooltip.add((int) renderSlot, Component.literal(("\u00A75Crystal Fortune " + new java.text.DecimalFormat("##").format(itemstack.getOrCreateTag().getDouble("ftwp_gminer")))));
					renderSlot = renderSlot + 1;
				}
				if (itemstack.getOrCreateTag().getDouble("ftw_kcn") > 0) {
					tooltip.add((int) renderSlot, Component.literal(("\u00A74xp : " + itemstack.getOrCreateTag().getDouble("ftw_kc") + "/ " + itemstack.getOrCreateTag().getDouble("ftw_kcn"))));
					renderSlot = renderSlot + 1;
				}
			} else {
				tooltip.add((int) renderSlot, Component.literal("Press <shift> to view more"));
			}
		}
	}
}
