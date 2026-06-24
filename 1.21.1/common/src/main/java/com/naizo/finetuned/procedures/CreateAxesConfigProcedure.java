package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateAxesConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "axe_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "extra_damage_chance")) {
			JaumlConfigLib.setNumberValue(folder, filename, "extra_damage_chance", 10);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "damage_amount")) {
			JaumlConfigLib.setNumberValue(folder, filename, "damage_amount", 8);
		}
	}
}
