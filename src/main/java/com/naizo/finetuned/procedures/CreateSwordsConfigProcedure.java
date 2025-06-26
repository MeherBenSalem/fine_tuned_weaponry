package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateSwordsConfigProcedure {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		execute();
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		String filename = "";
		String folder = "";
		folder = "finetunned/weapons";
		filename = "swords_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "helish_fire_aspect_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "helish_fire_aspect_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_strike_range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_strike_range", 20);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_strike_number")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_strike_number", 3);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_speed_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_speed_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_speed_duration")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_speed_duration", 100);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_haste_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_haste_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_haste_duration")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_haste_duration", 100);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "zenitsus_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "zenitsus_cooldown", 300);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "bone_sword_effect_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "bone_sword_effect_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "bone_sword_effect_duration")) {
			JaumlConfigLib.setNumberValue(folder, filename, "bone_sword_effect_duration", 60);
		}
	}
}
