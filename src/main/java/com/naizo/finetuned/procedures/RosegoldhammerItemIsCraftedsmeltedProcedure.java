package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class RosegoldhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		itemstack.enchant(Enchantments.SMITE, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "hammer_config", "rose_hammer_smite_level"));
		itemstack.enchant(Enchantments.BANE_OF_ARTHROPODS, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "hammer_config", "rose_hammer_boa_level"));
	}
}
