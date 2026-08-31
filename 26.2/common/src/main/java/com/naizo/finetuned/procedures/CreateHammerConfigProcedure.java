package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateHammerConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "hammer_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "earth_hammer_mending_oncraft")) {
			JaumlConfigLib.setBooleanValue(folder, filename, "earth_hammer_mending_oncraft", true);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "obsidian_hammer_unbreaking_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "obsidian_hammer_unbreaking_level", 2);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_smite_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_smite_level", 2);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_boa_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_boa_level", 2);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "earth_hammer_aoe_range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "earth_hammer_aoe_range", 5);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "earth_hammer_aoe_damage")) {
			JaumlConfigLib.setNumberValue(folder, filename, "earth_hammer_aoe_damage", 8);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "earth_hammer_knockback")) {
			JaumlConfigLib.setNumberValue(folder, filename, "earth_hammer_knockback", 1.2);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "earth_hammer_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "earth_hammer_cooldown", 100);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "obsidian_hammer_explosion_power")) {
			JaumlConfigLib.setNumberValue(folder, filename, "obsidian_hammer_explosion_power", 3);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "obsidian_hammer_fire_range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "obsidian_hammer_fire_range", 4);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "obsidian_hammer_fire_seconds")) {
			JaumlConfigLib.setNumberValue(folder, filename, "obsidian_hammer_fire_seconds", 5);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "obsidian_hammer_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "obsidian_hammer_cooldown", 300);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_heal_range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_heal_range", 6);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_heal_amount")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_heal_amount", 4);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_regen_duration")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_regen_duration", 100);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_regen_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_regen_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "rose_hammer_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "rose_hammer_cooldown", 400);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "hollow_staff_haste_duration")) {
			JaumlConfigLib.setNumberValue(folder, filename, "hollow_staff_haste_duration", 140);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "hollow_staff_haste_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "hollow_staff_haste_level", 1);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "hollow_staff_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "hollow_staff_cooldown", 250);
		}
	}
}
