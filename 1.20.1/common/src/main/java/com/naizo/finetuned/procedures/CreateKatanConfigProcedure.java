package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateKatanConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "katana_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "dash_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "dash_cooldown", 40);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "dash_power")) {
			JaumlConfigLib.setNumberValue(folder, filename, "dash_power", 2);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "blood_dash_cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "blood_dash_cooldown", 40);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "blood_dash_power")) {
			JaumlConfigLib.setNumberValue(folder, filename, "blood_dash_power", 2);
		}
	}
}
