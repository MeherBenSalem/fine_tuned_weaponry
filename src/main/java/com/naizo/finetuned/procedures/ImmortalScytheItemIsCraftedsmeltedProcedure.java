package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class ImmortalScytheItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		itemstack.enchant(Enchantments.SHARPNESS, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "scythe_config", "sharpness_level"));
		itemstack.enchant(Enchantments.UNBREAKING, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "scythe_config", "unbreaking_level"));
	}
}
