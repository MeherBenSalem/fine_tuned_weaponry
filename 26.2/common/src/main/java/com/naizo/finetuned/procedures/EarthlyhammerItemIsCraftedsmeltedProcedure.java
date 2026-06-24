package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.util.EnchantmentApplication;

public class EarthlyhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		if (JaumlConfigLib.getBooleanValue("fine_tuned_weaponry/weapons", "hammer_config", "earth_hammer_mending_oncraft")) {
			EnchantmentApplication.apply(world, itemstack, Enchantments.MENDING, 0);
		}
	}
}
