
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<CreativeModeTab> FINE_TUNED_WEAPONS = REGISTRY.register("fine_tuned_weapons",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.fine_tuned_weaponry.fine_tuned_weapons")).icon(() -> new ItemStack(FineTunedWeaponryModItems.CLASSIC_KATANA.get())).displayItems((parameters, tabData) -> {
				tabData.accept(FineTunedWeaponryModItems.CLASSIC_KATANA.get());
				tabData.accept(FineTunedWeaponryModItems.BLOOD_KATANA.get());
				tabData.accept(FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG.get());
				tabData.accept(FineTunedWeaponryModItems.KURASAI_KATANA.get());
				tabData.accept(FineTunedWeaponryModItems.HOLLOW_MAN_RUYI_JINGU_STAFF.get());
				tabData.accept(FineTunedWeaponryModItems.WEAPONTEMPLATE.get());
				tabData.accept(FineTunedWeaponryModItems.CLASSICHAMMER.get());
				tabData.accept(FineTunedWeaponryModItems.EARTHLYHAMMER.get());
				tabData.accept(FineTunedWeaponryModItems.OBSIDIANHAMMER.get());
				tabData.accept(FineTunedWeaponryModItems.ROSEGOLDHAMMER.get());
				tabData.accept(FineTunedWeaponryModItems.EARTHLY_INGOT.get());
				tabData.accept(FineTunedWeaponryModItems.OBSIDIAN_FORGED_INGOT.get());
				tabData.accept(FineTunedWeaponryModItems.ROSE_GOLD_INGOT.get());
			}).build());
}
