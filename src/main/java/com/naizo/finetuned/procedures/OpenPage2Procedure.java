package com.naizo.finetuned.procedures;

import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

import com.naizo.finetuned.world.inventory.BookPage3Menu;
import com.naizo.finetuned.network.FineTunedWeaponryModVariables;

public class OpenPage2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ServerPlayer _ent) {
			BlockPos _bpos = BlockPos.containing(x, y, z);
			NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("BookPage3");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new BookPage3Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
				}
			}, _bpos);
		}
		{
			double _setval = 2;
			entity.getCapability(FineTunedWeaponryModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.page = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
	}
}
