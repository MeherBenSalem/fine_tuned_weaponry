
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import com.naizo.finetuned.enchantment.SuperChargeEnchantEnchantment;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Enchantment> SUPER_CHARGE_ENCHANT = REGISTRY.register("super_charge_enchant", () -> new SuperChargeEnchantEnchantment());
}
