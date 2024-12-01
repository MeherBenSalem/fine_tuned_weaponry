package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class SetUpRequiredXpProcedure {
	public static double execute(ItemStack itemstack) {
		if (itemstack.getItem() == FineTunedWeaponryModItems.T_1_BARKPIERCER.get()) {
			return 100;
		} else if (itemstack.getItem() == FineTunedWeaponryModItems.T_2_BARKPIERCER.get()) {
			return 500;
		}
		return 0;
	}
}
