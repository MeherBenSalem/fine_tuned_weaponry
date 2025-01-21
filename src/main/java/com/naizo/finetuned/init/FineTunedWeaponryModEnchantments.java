
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import com.naizo.finetuned.enchantment.ThunderFuryEnchantment;
import com.naizo.finetuned.enchantment.StonefallEnchantment;
import com.naizo.finetuned.enchantment.LaunchstrikeEnchantment;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Enchantment> STONEFALL = REGISTRY.register("stonefall", () -> new StonefallEnchantment());
	public static final RegistryObject<Enchantment> LAUNCHSTRIKE = REGISTRY.register("launchstrike", () -> new LaunchstrikeEnchantment());
	public static final RegistryObject<Enchantment> THUNDER_FURY = REGISTRY.register("thunder_fury", () -> new ThunderFuryEnchantment());
}
