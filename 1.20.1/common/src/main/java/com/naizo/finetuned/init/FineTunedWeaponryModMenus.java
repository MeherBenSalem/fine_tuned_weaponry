package com.naizo.finetuned.init;

import com.naizo.finetuned.registry.RegistryHolder;
import com.naizo.finetuned.world.inventory.ResearchTableGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import net.minecraft.world.inventory.MenuType;

public final class FineTunedWeaponryModMenus {
	public static final RegistryHolder<MenuType<WeaponsForgeGUIMenu>> WEAPONS_FORGE_GUI = new RegistryHolder<>("weapons_forge_gui");
	public static final RegistryHolder<MenuType<WeaponsAnvilGUIMenu>> WEAPONS_ANVIL_GUI = new RegistryHolder<>("weapons_anvil_gui");
	public static final RegistryHolder<MenuType<ResearchTableGUIMenu>> RESEARCH_TABLE_GUI = new RegistryHolder<>("research_table_gui");

	private FineTunedWeaponryModMenus() {
	}
}
