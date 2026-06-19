package com.naizo.finetuned.forge.platform;

import com.naizo.finetuned.platform.services.IMenuHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

public class ForgeMenuHelper implements IMenuHelper {
	@Override
	public void openMenu(ServerPlayer player, BlockPos pos, MenuProvider provider) {
		NetworkHooks.openScreen(player, provider, pos);
	}
}
