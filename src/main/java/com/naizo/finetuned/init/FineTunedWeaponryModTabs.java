
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import com.naizo.finetuned.FineTunedWeaponryMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FineTunedWeaponryModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FineTunedWeaponryMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
			tabData.accept(FineTunedWeaponryModItems.BARKSHIELD.get());
			tabData.accept(FineTunedWeaponryModItems.BARKSHIELD_T_2.get());
			tabData.accept(FineTunedWeaponryModItems.BARKSHIELD_T_3.get());
			tabData.accept(FineTunedWeaponryModItems.BARKBOW.get());
			tabData.accept(FineTunedWeaponryModItems.T_1_BARK_ARROW.get());
			tabData.accept(FineTunedWeaponryModItems.IRONCLAD_FURY_1.get());
			tabData.accept(FineTunedWeaponryModItems.IRONCLAD_FURY_2.get());
			tabData.accept(FineTunedWeaponryModItems.IRONCLAD_FURY_3.get());
			tabData.accept(FineTunedWeaponryModItems.CANDY_CANE_SWORD.get());
			tabData.accept(FineTunedWeaponryModItems.SANTASHELMET_HELMET.get());
			tabData.accept(FineTunedWeaponryModItems.SANTASHELMET_CHESTPLATE.get());
			tabData.accept(FineTunedWeaponryModItems.SANTASHELMET_LEGGINGS.get());
			tabData.accept(FineTunedWeaponryModItems.SANTASHELMET_BOOTS.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			tabData.accept(FineTunedWeaponryModItems.T_1_REFINED_CRYSTAL.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
			tabData.accept(FineTunedWeaponryModItems.CANADY_CANE.get());
			tabData.accept(FineTunedWeaponryModItems.GINGER_BREAD.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(FineTunedWeaponryModItems.GINGER_BREAD_MACE.get());
			tabData.accept(FineTunedWeaponryModItems.GRANDMASTERLIGHTPOLE.get());
		}
	}
}
