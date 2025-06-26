package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

public class EarthlyhammerItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		if (JaumlConfigLib.getBooleanValue("finetunned/weapons", "hammer_config", "earth_hammer_mending_oncraft")) {
			itemstack.enchant(Enchantments.MENDING, 0);
		}
	}
}
