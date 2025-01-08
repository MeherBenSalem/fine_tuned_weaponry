package com.naizo.finetuned.init;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.naizo.finetuned.configuration.PoolConfigConfiguration;
import com.naizo.finetuned.configuration.KatanaConfigConfiguration;
import com.naizo.finetuned.configuration.HammerConfigConfiguration;
import com.naizo.finetuned.configuration.FangConfigConfiguration;
import com.naizo.finetuned.FineTunedWeaponryMod;

@Mod.EventBusSubscriber(modid = FineTunedWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FineTunedWeaponryModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PoolConfigConfiguration.SPEC, "Finetuned/Pools/pools_config.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, KatanaConfigConfiguration.SPEC, "Finetuned/Katanas/katanas_config.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FangConfigConfiguration.SPEC, "Finetuned/Fangs/fangs_config.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HammerConfigConfiguration.SPEC, "Finetuned/Hammers/hammers_config.toml");
		});
	}
}
