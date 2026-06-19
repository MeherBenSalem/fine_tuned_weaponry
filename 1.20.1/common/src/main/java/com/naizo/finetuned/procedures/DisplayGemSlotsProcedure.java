package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.GemNbtKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DisplayGemSlotsProcedure {
	public static void execute(ItemStack itemstack, List<Component> tooltip, boolean shiftDown) {
		if (tooltip == null || !GemNbtKeys.isModified(itemstack)) {
			return;
		}
		if (shiftDown) {
			tooltip.add(Component.literal("\u00A76Finetunned Upgrades"));
			if (GemNbtKeys.hasGem(itemstack, GemNbtKeys.INFERNO_CORE)) {
				tooltip.add(Component.literal("\u00A74Infernal Core"));
				appendIfPresent(tooltip, itemstack, GemNbtKeys.BLAZING_AMPLIFIER, "\u00A74- Blazing Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.WILDFIRE_AMPLIFIER, "\u00A74- Wildfire Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.HELLFIRE_AMPLIFIER, "\u00A74- Hellfire Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.PYROCLASM_AMPLIFIER, "\u00A74- Pyroclasm Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.INFERNAL_HUNGER_AMPLIFIER, "\u00A74- Infernal Hunger Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.LAVA_INFUSION_AMPLIFIER, "\u00A74- Lava Infusion Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.SMOKESCREEN_AMPLIFIER, "\u00A74- Smokescreen Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.VOLCANIC_BURST_AMPLIFIER, "\u00A74- Volcanic Burst Amplifier");
			}
			if (GemNbtKeys.hasGem(itemstack, GemNbtKeys.STORM_SHARD)) {
				tooltip.add(Component.literal("\u00A7eStorm Shard"));
				appendIfPresent(tooltip, itemstack, GemNbtKeys.CHARGED_AMPLIFIER, "\u00A76- Charged Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.OVERLOAD_AMPLIFIER, "\u00A76- Overload Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.SUPERSTORM_AMPLIFIER, "\u00A76- Superstorm Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.STORM_FURY_AMPLIFIER, "\u00A76- Storm Fury Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.STATIC_AMPLIFIER, "\u00A76- Static Amplifier");
			}
			if (GemNbtKeys.hasGem(itemstack, GemNbtKeys.FROST_RUNE)) {
				tooltip.add(Component.literal("\u00A73Frost Rune"));
				appendIfPresent(tooltip, itemstack, GemNbtKeys.GLACIAL_AMPLIFIER, "\u00A71- Glacial Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.PERMAFROST_AMPLIFIER, "\u00A71- Permafrost Amplifier");
				appendIfPresent(tooltip, itemstack, GemNbtKeys.BLIZZARD_AMPLIFIER, "\u00A71- Blizzard Amplifier");
			}
		} else {
			tooltip.add(Component.literal("<Press shift to show more>"));
		}
	}

	private static void appendIfPresent(List<Component> tooltip, ItemStack stack, String key, String text) {
		if (GemNbtKeys.hasGem(stack, key)) {
			tooltip.add(Component.literal(text));
		}
	}
}
