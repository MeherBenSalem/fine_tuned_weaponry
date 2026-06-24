package com.naizo.finetuned.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;

public final class EnchantmentApplication {
	private EnchantmentApplication() {
	}

	public static void apply(LevelAccessor world, ItemStack stack, ResourceKey<Enchantment> enchantment, int level) {
		Holder<Enchantment> holder = holder(world, enchantment);
		EnchantmentHelper.updateEnchantments(stack, builder -> builder.set(holder, level));
	}

	public static void remove(LevelAccessor world, ItemStack stack, ResourceKey<Enchantment> enchantment) {
		Holder<Enchantment> holder = holder(world, enchantment);
		EnchantmentHelper.updateEnchantments(stack, builder -> builder.set(holder, 0));
	}

	public static Holder<Enchantment> holder(LevelAccessor world, ResourceKey<Enchantment> enchantment) {
		return lookup(world).getOrThrow(enchantment);
	}

	public static Holder<Enchantment> holder(LevelAccessor world, ResourceLocation id) {
		return lookup(world).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, id));
	}

	private static HolderLookup.RegistryLookup<Enchantment> lookup(LevelAccessor world) {
		return world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
	}
}
