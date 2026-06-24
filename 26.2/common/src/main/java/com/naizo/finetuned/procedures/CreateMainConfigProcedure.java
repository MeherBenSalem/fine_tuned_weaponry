package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

public class CreateMainConfigProcedure {
	public static void execute() {
		String folder = "fine_tuned_weaponry";
		String filename = "main_config";
		JaumlConfigLib.createConfigFile(folder, filename);
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "forge_ignite_item")) {
			JaumlConfigLib.setStringValue(folder, filename, "forge_ignite_item", "minecraft:coal_block");
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "forge_timer")) {
			JaumlConfigLib.setNumberValue(folder, filename, "forge_timer", 120);
		}
	}
}
