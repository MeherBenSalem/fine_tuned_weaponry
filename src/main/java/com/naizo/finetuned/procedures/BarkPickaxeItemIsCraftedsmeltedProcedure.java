package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class BarkPickaxeItemIsCraftedsmeltedProcedure {
	public static void execute(ItemStack itemstack) {
		if (!itemstack.getOrCreateTag().getBoolean("ftw_en")) {
			itemstack.getOrCreateTag().putBoolean("ftw_en", true);
			AddGemMinerEffectProcedure.execute(itemstack);
		}
	}
}
