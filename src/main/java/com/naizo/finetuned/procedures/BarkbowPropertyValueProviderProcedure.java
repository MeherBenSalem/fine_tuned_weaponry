package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class BarkbowPropertyValueProviderProcedure {
	public static double execute(ItemStack itemstack) {
		return itemstack.getOrCreateTag().getDouble("BowPull") / 20;
	}
}
