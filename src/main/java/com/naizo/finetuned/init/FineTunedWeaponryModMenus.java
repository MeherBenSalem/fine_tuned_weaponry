
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import com.naizo.finetuned.world.inventory.ResearchTableUIMenu;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<MenuType<WeaponsForgeGUIMenu>> WEAPONS_FORGE_GUI = REGISTRY.register("weapons_forge_gui", () -> IForgeMenuType.create(WeaponsForgeGUIMenu::new));
	public static final RegistryObject<MenuType<ResearchTableUIMenu>> RESEARCH_TABLE_UI = REGISTRY.register("research_table_ui", () -> IForgeMenuType.create(ResearchTableUIMenu::new));
}
