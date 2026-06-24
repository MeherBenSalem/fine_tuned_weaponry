package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateSwordsConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "swords_config";
		JaumlConfigLib.createConfigFile(folder, filename);
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
