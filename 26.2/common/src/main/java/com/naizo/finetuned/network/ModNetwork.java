package com.naizo.finetuned.network;

import com.naizo.finetuned.platform.Services;

public final class ModNetwork {
	public static void init() {
		Services.NETWORK.registerPacket(PlayerVariables.SyncMessage.class,
				PlayerVariables.SyncMessage::encode,
				PlayerVariables.SyncMessage::new,
				PlayerVariables.SyncMessage::handle);
		Services.NETWORK.registerPacket(WeaponsForgeGUIButtonMessage.class,
				WeaponsForgeGUIButtonMessage::buffer,
				WeaponsForgeGUIButtonMessage::new,
				WeaponsForgeGUIButtonMessage::handlePacket);
		Services.NETWORK.registerPacket(WeaponsAnvilGUIButtonMessage.class,
				WeaponsAnvilGUIButtonMessage::buffer,
				WeaponsAnvilGUIButtonMessage::new,
				WeaponsAnvilGUIButtonMessage::handlePacket);
		Services.NETWORK.registerPacket(ResearchTableGUIButtonMessage.class,
				ResearchTableGUIButtonMessage::buffer,
				ResearchTableGUIButtonMessage::new,
				ResearchTableGUIButtonMessage::handlePacket);
	}

	private ModNetwork() {
	}
}
