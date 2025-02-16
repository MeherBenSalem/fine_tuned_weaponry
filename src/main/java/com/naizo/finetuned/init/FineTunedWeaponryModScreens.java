
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
import com.naizo.finetuned.client.gui.ResearchTableUIScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FineTunedWeaponryModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI.get(), WeaponsForgeGUIScreen::new);
			MenuScreens.register(FineTunedWeaponryModMenus.RESEARCH_TABLE_UI.get(), ResearchTableUIScreen::new);
		});
	}
}
