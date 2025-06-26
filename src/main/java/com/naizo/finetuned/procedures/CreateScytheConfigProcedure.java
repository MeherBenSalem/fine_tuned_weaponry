package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateScytheConfigProcedure {
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
		filename = "scythe_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "sharpness_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "sharpness_level", 3);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "unbreaking_level")) {
			JaumlConfigLib.setNumberValue(folder, filename, "unbreaking_level", 3);
		}
	}
}
