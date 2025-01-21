package com.naizo.finetuned.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class RosegoldhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		itemstack.enchant(Enchantments.BANE_OF_ARTHROPODS, 2);
		itemstack.enchant(Enchantments.SMITE, 2);
	}
}
