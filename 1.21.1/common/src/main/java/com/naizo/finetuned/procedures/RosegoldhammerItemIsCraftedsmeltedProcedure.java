package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.util.EnchantmentApplication;

public class RosegoldhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		EnchantmentApplication.apply(world, itemstack, Enchantments.SMITE, (int) JaumlConfigLib.getNumberValue("fine_tuned_weaponry/weapons", "hammer_config", "rose_hammer_smite_level"));
		EnchantmentApplication.apply(world, itemstack, Enchantments.BANE_OF_ARTHROPODS, (int) JaumlConfigLib.getNumberValue("fine_tuned_weaponry/weapons", "hammer_config", "rose_hammer_boa_level"));
	}
}
