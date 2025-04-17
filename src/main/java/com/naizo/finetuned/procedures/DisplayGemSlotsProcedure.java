package com.naizo.finetuned.procedures;

import net.minecraftforge.registries.ForgeRegistries;
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

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

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
				if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNO_CORE.get()).toString()))) {
					tooltip.add(Component.literal("\u00A74Infernal Core"));
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Blazing Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Wildfire Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Hellfire Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Pyroclasm Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Infernal Hunger Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Lava Infusion Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Smokescreen Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A74- Volcanic Burst Amplifier"));
					}
				}
				if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.STORM_SHARD.get()).toString()))) {
					tooltip.add(Component.literal("\u00A7eStorm Shard"));
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A76- Charged Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A76- Overload Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A76- Superstorm Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A76- Storm Fury Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.STATIC_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A76- Static Amplifier"));
					}
				}
				if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.FROST_RUNE.get()).toString()))) {
					tooltip.add(Component.literal("\u00A73Frost Rune"));
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A71- Glacial Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A71- Permafrost Amplifier"));
					}
					if (itemstack.getOrCreateTag().getBoolean((ForgeRegistries.ITEMS.getKey(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER.get()).toString()))) {
						tooltip.add(Component.literal("\u00A71- Blizzard Amplifier"));
					}
				}
			} else {
				tooltip.add(Component.literal("<Press shift to show more>"));
			}
		}
	}
}
