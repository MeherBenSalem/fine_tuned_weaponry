package com.naizo.finetuned.init;

import com.naizo.finetuned.enchantment.SuperChargeEnchantEnchantment;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.world.item.enchantment.Enchantment;

public final class FineTunedWeaponryModEnchantments {
	public static final RegistryHolder<Enchantment> SUPER_CHARGE_ENCHANT =
			new RegistryHolder<>("super_charge_enchant", SuperChargeEnchantEnchantment::new);

	private FineTunedWeaponryModEnchantments() {
	}
}
