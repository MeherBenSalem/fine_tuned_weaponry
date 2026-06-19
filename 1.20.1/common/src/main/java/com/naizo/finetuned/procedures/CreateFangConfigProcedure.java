package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateFangConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry/weapons";
		String filename = "fang_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "cooldown", 300);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "range", 10);
		}
	}
}
