package com.naizo.finetuned.platform.services;

import com.naizo.finetuned.network.PlayerVariables;
import net.minecraft.world.entity.player.Player;

public interface IPlayerVariablesAccess {
	PlayerVariables get(Player player);

	void sync(Player player);

	void copyOnClone(Player original, Player clone, boolean wasDeath);
}
