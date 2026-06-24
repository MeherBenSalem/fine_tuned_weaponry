package com.naizo.finetuned.procedures;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import com.naizo.finetuned.util.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tn.naizo.jauml.JaumlConfigLib;

import java.util.List;

public final class GiveWeaponTooltipProcedure {
	private GiveWeaponTooltipProcedure() {
	}

	public static void execute(ItemStack itemstack, List<Component> tooltip) {
		if (tooltip == null || itemstack.isEmpty()) {
			return;
		}
		if (itemstack.getItem() == FineTunedWeaponryModItems.CLASSIC_KATANA.get()) {
			tooltip.add(3, Component.literal("\u00A79Cooldown : " + JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "katana_config", "dash_cooldown") * 0.05 + " s"));
		} else if (itemstack.getItem() == FineTunedWeaponryModItems.BLOOD_KATANA.get()) {
			tooltip.add(3, Component.literal("\u00A79Cooldown : " + JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "katana_config", "blood_dash_cooldown") * 0.05 + " s"));
		} else if (itemstack.getItem() == FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG.get()) {
			tooltip.add(3, Component.literal("\u00A79Cooldown : " + JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "fang_config", "cooldown") * 0.05 + " s"));
		}
	}
}
