package com.naizo.finetuned.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class ImmortalScytheItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		itemstack.enchant(Enchantments.SHARPNESS, 3);
		itemstack.enchant(Enchantments.UNBREAKING, 3);
	}
}
