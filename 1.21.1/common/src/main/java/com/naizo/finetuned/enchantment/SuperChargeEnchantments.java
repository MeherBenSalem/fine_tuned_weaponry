package com.naizo.finetuned.enchantment;

import com.naizo.finetuned.Constants;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class SuperChargeEnchantments {
	private SuperChargeEnchantments() {
	}

	public static Enchantment create(RegistryAccess registryAccess) {
		ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT,
				ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "super_charge_enchant"));
		HolderSet<net.minecraft.world.item.Item> weapons = registryAccess.lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.SWORDS);
		return Enchantment.enchantment(
				Enchantment.definition(
						weapons,
						weapons,
						10,
						4,
						Enchantment.dynamicCost(1, 10),
						Enchantment.dynamicCost(6, 10),
						1,
						EquipmentSlotGroup.MAINHAND
				)
		).build(key.location());
	}
}
