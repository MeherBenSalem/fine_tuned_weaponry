package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.util.EnchantmentApplication;

public class HelishSwordItemIsCraftedsmeltedProcedure {
	public static void execute(LevelAccessor world, ItemStack itemstack) {
		EnchantmentApplication.apply(world, itemstack, Enchantments.FIRE_ASPECT, (int) JaumlConfigLib.getNumberValue("fine_tuned_weaponry/weapons", "swords_config", "helish_fire_aspect_level"));
	}
}
