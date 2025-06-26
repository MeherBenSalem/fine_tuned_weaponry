package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateKatanConfigProcedure {
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
		filename = "katana_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
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
