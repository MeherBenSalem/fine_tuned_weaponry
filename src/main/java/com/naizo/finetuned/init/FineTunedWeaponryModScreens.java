
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import com.naizo.finetuned.client.gui.WeaponsForgeGUIScreen;
import com.naizo.finetuned.client.gui.WeaponsAnvilGUIScreen;
import com.naizo.finetuned.client.gui.ResearchTableGUIScreen;
import com.naizo.finetuned.client.gui.BookTitleScreen;
import com.naizo.finetuned.client.gui.BookPage6Screen;
import com.naizo.finetuned.client.gui.BookPage5Screen;
import com.naizo.finetuned.client.gui.BookPage4Screen;
import com.naizo.finetuned.client.gui.BookPage3Screen;
import com.naizo.finetuned.client.gui.BookPage2Screen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FineTunedWeaponryModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI.get(), WeaponsForgeGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.get(), WeaponsAnvilGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI.get(), ResearchTableGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_PAGE_2.get(), BookPage2Screen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_TITLE.get(), BookTitleScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_PAGE_3.get(), BookPage3Screen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_PAGE_4.get(), BookPage4Screen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_PAGE_5.get(), BookPage5Screen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.BOOK_PAGE_6.get(), BookPage6Screen::new);
		});
	}
}
