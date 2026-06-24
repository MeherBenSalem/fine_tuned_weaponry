package com.naizo.finetuned.neoforge;

import com.naizo.finetuned.gui.ResearchTableGUIScreen;
import com.naizo.finetuned.gui.WeaponsAnvilGUIScreen;
import com.naizo.finetuned.gui.WeaponsForgeGUIScreen;
import com.naizo.finetuned.init.FineTunedWeaponryModMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class NeoForgeClientSetup {
	private NeoForgeClientSetup() {
	}

	@SubscribeEvent
	public static void onRegisterScreens(RegisterMenuScreensEvent event) {
		event.register(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI.get(), WeaponsForgeGUIScreen::new);
		event.register(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.get(), WeaponsAnvilGUIScreen::new);
		event.register(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI.get(), ResearchTableGUIScreen::new);
	}
}
