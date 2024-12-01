
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import com.naizo.finetuned.world.inventory.FTWAscensionGUIMenu;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<MenuType<FTWAscensionGUIMenu>> FTW_ASCENSION_GUI = REGISTRY.register("ftw_ascension_gui", () -> IForgeMenuType.create(FTWAscensionGUIMenu::new));
}
