package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreateMainConfigProcedure {
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
		folder = "finetunned";
		filename = "main_config";
		if (JaumlConfigLib.createConfigFile(folder, filename)) {
			JaumlConfigLib.createConfigFile(folder, filename);
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "forge_ignite_item")) {
			JaumlConfigLib.setStringValue(folder, filename, "forge_ignite_item", "minecraft:coal_block");
		}
		if (!JaumlConfigLib.arrayKeyExists(folder, filename, "forge_timer")) {
			JaumlConfigLib.setNumberValue(folder, filename, "forge_timer", 120);
		}
	}
}
