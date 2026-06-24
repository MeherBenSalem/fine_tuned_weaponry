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
	}
}
