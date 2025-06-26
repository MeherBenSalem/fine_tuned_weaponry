package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateHammerConfigProcedure {
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
		filename = "hammer_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
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
