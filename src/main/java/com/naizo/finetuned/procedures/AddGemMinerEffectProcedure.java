package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

public class AddGemMinerEffectProcedure {
	public static void execute(ItemStack itemstack) {
		if (itemstack.getOrCreateTag().getDouble("ftwp_gminer") < 5) {
			itemstack.getOrCreateTag().putDouble("ftwp_gminer", (itemstack.getOrCreateTag().getDouble("ftwp_gminer") + 1));
		}
	}
}
