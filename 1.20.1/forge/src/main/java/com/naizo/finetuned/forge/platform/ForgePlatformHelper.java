package com.naizo.finetuned.forge.platform;

import com.naizo.finetuned.platform.services.IPlatformHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "Forge";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return net.minecraftforge.fml.ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLEnvironment.production;
	}
}
