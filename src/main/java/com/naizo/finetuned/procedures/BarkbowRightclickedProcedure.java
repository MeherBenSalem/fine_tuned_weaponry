package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class BarkbowRightclickedProcedure {
	public static void execute(ItemStack itemstack) {
		itemstack.getOrCreateTag().putDouble("BowPull", (itemstack.getOrCreateTag().getDouble("BowPull") + 1));
	}
}
