package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class EnableFTWProcedure {
	public static void execute(ItemStack itemstack) {
		if (!itemstack.getOrCreateTag().getBoolean("ftw_en")) {
			itemstack.getOrCreateTag().putBoolean("ftw_en", true);
			itemstack.getOrCreateTag().putDouble("ftw_kcn", SetUpRequiredXpProcedure.execute(itemstack));
		}
	}
}
