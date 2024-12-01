package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class BarkbowOnPlayerStoppedUsingProcedure {
	public static void execute(ItemStack itemstack) {
		itemstack.getOrCreateTag().putDouble("BowPull", 0);
	}
}
