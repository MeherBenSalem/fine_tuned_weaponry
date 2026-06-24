package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.util.EnchantmentApplication;

public class ImmortalScytheItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		EnchantmentApplication.apply(world, itemstack, Enchantments.SHARPNESS, (int) JaumlConfigLib.getNumberValue("fine_tuned_weaponry/weapons", "scythe_config", "sharpness_level"));
		EnchantmentApplication.apply(world, itemstack, Enchantments.UNBREAKING, (int) JaumlConfigLib.getNumberValue("fine_tuned_weaponry/weapons", "scythe_config", "unbreaking_level"));
	}
}
