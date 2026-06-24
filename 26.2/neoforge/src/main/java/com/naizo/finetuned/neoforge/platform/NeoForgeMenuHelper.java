package com.naizo.finetuned.neoforge.platform;

import com.naizo.finetuned.platform.services.IMenuHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public class NeoForgeMenuHelper implements IMenuHelper {
	@Override
	public void openMenu(ServerPlayer player, BlockPos pos, MenuProvider provider) {
		player.openMenu(provider, pos);
	}
}
