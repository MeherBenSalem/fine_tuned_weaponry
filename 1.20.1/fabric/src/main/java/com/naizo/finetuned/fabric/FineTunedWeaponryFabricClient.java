package com.naizo.finetuned.fabric;

import com.naizo.finetuned.gui.ResearchTableGUIScreen;
import com.naizo.finetuned.gui.WeaponsAnvilGUIScreen;
import com.naizo.finetuned.gui.WeaponsForgeGUIScreen;
import com.naizo.finetuned.init.FineTunedWeaponryModMenus;
import com.naizo.finetuned.procedures.DisplayGemSlotsProcedure;
import com.naizo.finetuned.procedures.GiveWeaponTooltipProcedure;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;

public class FineTunedWeaponryFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI.get(), WeaponsForgeGUIScreen::new);
		MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.get(), WeaponsAnvilGUIScreen::new);
		MenuScreens.register(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI.get(), ResearchTableGUIScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			GiveWeaponTooltipProcedure.execute(stack, lines);
			DisplayGemSlotsProcedure.execute(stack, lines, Screen.hasShiftDown());
		});
	}
}