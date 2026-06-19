package com.naizo.finetuned.forge;

import com.naizo.finetuned.gui.ResearchTableGUIScreen;
import com.naizo.finetuned.gui.WeaponsAnvilGUIScreen;
import com.naizo.finetuned.gui.WeaponsForgeGUIScreen;
import com.naizo.finetuned.init.FineTunedWeaponryModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ForgeClientSetup {
	private ForgeClientSetup() {
	}

	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI.get(), WeaponsForgeGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.get(), WeaponsAnvilGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI.get(), ResearchTableGUIScreen::new);
		});
	}
}
