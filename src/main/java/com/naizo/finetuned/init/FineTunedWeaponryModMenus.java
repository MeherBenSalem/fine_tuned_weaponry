
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
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import com.naizo.finetuned.world.inventory.ResearchTableGUIMenu;
import com.naizo.finetuned.world.inventory.BookTitleMenu;
import com.naizo.finetuned.world.inventory.BookPage6Menu;
import com.naizo.finetuned.world.inventory.BookPage5Menu;
import com.naizo.finetuned.world.inventory.BookPage4Menu;
import com.naizo.finetuned.world.inventory.BookPage3Menu;
import com.naizo.finetuned.world.inventory.BookPage2Menu;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<MenuType<WeaponsForgeGUIMenu>> WEAPONS_FORGE_GUI = REGISTRY.register("weapons_forge_gui", () -> IForgeMenuType.create(WeaponsForgeGUIMenu::new));
	public static final RegistryObject<MenuType<WeaponsAnvilGUIMenu>> WEAPONS_ANVIL_GUI = REGISTRY.register("weapons_anvil_gui", () -> IForgeMenuType.create(WeaponsAnvilGUIMenu::new));
	public static final RegistryObject<MenuType<ResearchTableGUIMenu>> RESEARCH_TABLE_GUI = REGISTRY.register("research_table_gui", () -> IForgeMenuType.create(ResearchTableGUIMenu::new));
	public static final RegistryObject<MenuType<BookPage2Menu>> BOOK_PAGE_2 = REGISTRY.register("book_page_2", () -> IForgeMenuType.create(BookPage2Menu::new));
	public static final RegistryObject<MenuType<BookTitleMenu>> BOOK_TITLE = REGISTRY.register("book_title", () -> IForgeMenuType.create(BookTitleMenu::new));
	public static final RegistryObject<MenuType<BookPage3Menu>> BOOK_PAGE_3 = REGISTRY.register("book_page_3", () -> IForgeMenuType.create(BookPage3Menu::new));
	public static final RegistryObject<MenuType<BookPage4Menu>> BOOK_PAGE_4 = REGISTRY.register("book_page_4", () -> IForgeMenuType.create(BookPage4Menu::new));
	public static final RegistryObject<MenuType<BookPage5Menu>> BOOK_PAGE_5 = REGISTRY.register("book_page_5", () -> IForgeMenuType.create(BookPage5Menu::new));
	public static final RegistryObject<MenuType<BookPage6Menu>> BOOK_PAGE_6 = REGISTRY.register("book_page_6", () -> IForgeMenuType.create(BookPage6Menu::new));
}
