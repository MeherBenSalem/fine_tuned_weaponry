package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateScytheConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "scythe_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "sharpness_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "sharpness_level", 3);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "unbreaking_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "unbreaking_level", 3);
		}
	}
}
