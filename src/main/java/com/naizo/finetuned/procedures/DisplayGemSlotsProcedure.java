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
public class DisplayGemSlotsProcedure {
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
		if (itemstack.getOrCreateTag().getBoolean("finetunned")) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("\u00A76Finetunned Upgrades"));
				if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:inferno_core")) {
					tooltip.add(Component.literal("\u00A74Infernal Core"));
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:blazing_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Blazing Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:wildfire_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Wildfire Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:hellfire_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Hellfire Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:pyroclasm_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Pyroclasm Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:infernal_hunger_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Infernal Hunger Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:lava_infusion_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Lava Infusion Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:smokescreen_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Smokescreen Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:volcanic_burst_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Volcanic Burst Amplifier"));
					}
				}
				if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:frost_rune")) {
					tooltip.add(Component.literal("\u00A79Frost Rune"));
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:charged_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Charged Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:overload_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Overload Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:superstorm_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Superstorm Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:storm_fury_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Storm Fury Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:static_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Static Amplifier"));
					}
				}
				if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:storm_shard")) {
					tooltip.add(Component.literal("\u00A7eStorm Shard"));
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:glacial_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Glacial Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:permafrost_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Permafrost Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean("fine_tuned_weaponry:blizzard_amplifier")) {
						tooltip.add(Component.literal("\u00A74- Blizzard Amplifier"));
					}
				}
			} else {
				tooltip.add(Component.literal("<Press Shift To Show Upgrades>"));
			}
		}
	}
}
