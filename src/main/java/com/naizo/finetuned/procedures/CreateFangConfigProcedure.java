package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateFangConfigProcedure {
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
		filename = "fang_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "cooldown")) {
			JaumlConfigLib.setNumberValue(folder, filename, "cooldown", 300);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "range")) {
			JaumlConfigLib.setNumberValue(folder, filename, "range", 10);
		}
	}
}
