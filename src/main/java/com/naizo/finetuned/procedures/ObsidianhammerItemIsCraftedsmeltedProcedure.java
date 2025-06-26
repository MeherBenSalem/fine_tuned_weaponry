package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class ObsidianhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		itemstack.enchant(Enchantments.UNBREAKING, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "hammer_config", "obsidian_hammer_unbreaking_level"));
	}
}
