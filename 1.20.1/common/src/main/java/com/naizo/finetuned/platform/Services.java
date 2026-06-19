package com.naizo.finetuned.platform;

import com.naizo.finetuned.platform.services.IMenuHelper;
import com.naizo.finetuned.platform.services.INetworkHelper;
import com.naizo.finetuned.platform.services.IPlatformHelper;
import com.naizo.finetuned.platform.services.IPlayerVariablesAccess;

import java.util.ServiceLoader;

public final class Services {
	public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
	public static volatile INetworkHelper NETWORK;
	public static volatile IPlayerVariablesAccess PLAYER_VARIABLES;
	public static volatile IMenuHelper MENUS;

	private Services() {
	}

	public static <T> T load(Class<T> clazz) {
		return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
	}
}
