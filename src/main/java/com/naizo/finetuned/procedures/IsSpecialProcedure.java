package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class IsSpecialProcedure {
	public static boolean execute(ItemStack itemstack) {
		if (itemstack.getItem() == FineTunedWeaponryModItems.T_1_BARKPIERCER.get() || itemstack.getItem() == FineTunedWeaponryModItems.T_2_BARKPIERCER.get() || itemstack.getItem() == FineTunedWeaponryModItems.T_3_BARKPIERCER.get()) {
			return true;
		}
		return false;
	}
}
