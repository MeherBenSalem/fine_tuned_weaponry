package com.naizo.finetuned.neoforge;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.FineTunedWeaponry;
import com.naizo.finetuned.neoforge.network.NeoForgeNetwork;
import com.naizo.finetuned.neoforge.platform.NeoForgeMenuHelper;
import com.naizo.finetuned.neoforge.platform.NeoForgePlayerVariables;
import com.naizo.finetuned.neoforge.registry.NeoForgeModRegistry;
import com.naizo.finetuned.network.ModNetwork;
import com.naizo.finetuned.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class FineTunedWeaponryNeoForge {
	public FineTunedWeaponryNeoForge(IEventBus modBus) {
		Services.NETWORK = new NeoForgeNetwork();
		NeoForgeNetwork network = (NeoForgeNetwork) Services.NETWORK;
		Services.PLAYER_VARIABLES = new NeoForgePlayerVariables();
		Services.MENUS = new NeoForgeMenuHelper();
		NeoForgeModRegistry.register(modBus);
		NeoForgePlayerVariables.register(modBus);
		ModNetwork.init();
		modBus.addListener(network::registerPayloads);
		modBus.addListener(NeoForgeClientSetup::onRegisterScreens);
		NeoForge.EVENT_BUS.register(new NeoForgeModEvents());
		FineTunedWeaponry.init();
		Constants.LOG.info("Loaded {} on NeoForge", Constants.MOD_NAME);
	}
}
