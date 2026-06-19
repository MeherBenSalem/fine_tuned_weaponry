package com.naizo.finetuned.fabric;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.FineTunedWeaponry;
import com.naizo.finetuned.fabric.network.FabricNetwork;
import com.naizo.finetuned.fabric.platform.FabricMenuHelper;
import com.naizo.finetuned.fabric.platform.FabricPlayerVariables;
import com.naizo.finetuned.fabric.registry.FabricModRegistry;
import com.naizo.finetuned.network.ModNetwork;
import com.naizo.finetuned.platform.Services;
import net.fabricmc.api.ModInitializer;

public class FineTunedWeaponryFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Services.NETWORK = new FabricNetwork();
		Services.PLAYER_VARIABLES = new FabricPlayerVariables();
		Services.MENUS = new FabricMenuHelper();
		FabricModRegistry.register();
		ModNetwork.init();
		FabricModEvents.register();
		FineTunedWeaponry.init();
		Constants.LOG.info("Loaded {} on Fabric", Constants.MOD_NAME);
	}
}
