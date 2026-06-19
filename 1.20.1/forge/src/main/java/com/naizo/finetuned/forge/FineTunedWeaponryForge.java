package com.naizo.finetuned.forge;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.FineTunedWeaponry;
import com.naizo.finetuned.forge.network.ForgeNetwork;
import com.naizo.finetuned.forge.platform.ForgeMenuHelper;
import com.naizo.finetuned.forge.platform.ForgePlayerVariables;
import com.naizo.finetuned.forge.registry.ForgeModRegistry;
import com.naizo.finetuned.network.ModNetwork;
import com.naizo.finetuned.platform.Services;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class FineTunedWeaponryForge {
	public FineTunedWeaponryForge() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		Services.NETWORK = new ForgeNetwork();
		Services.PLAYER_VARIABLES = new ForgePlayerVariables();
		Services.MENUS = new ForgeMenuHelper();
		ForgeModRegistry.register(modBus);
		ModNetwork.init();
		modBus.addListener(ForgeClientSetup::onClientSetup);
		MinecraftForge.EVENT_BUS.register(new ForgeModEvents());
		FineTunedWeaponry.init();
		Constants.LOG.info("Loaded {} on Forge", Constants.MOD_NAME);
	}
}
