package com.naizo.finetuned.network;

import com.naizo.finetuned.procedures.InsertGemsProcedure;
import com.naizo.finetuned.procedures.RemoveGemProcedure;
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class WeaponsAnvilGUIButtonMessage {
	private final int buttonID, x, y, z;

	public WeaponsAnvilGUIButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public WeaponsAnvilGUIButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(WeaponsAnvilGUIButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handlePacket(WeaponsAnvilGUIButtonMessage message, boolean isClientSide) {
	}

	public static void handleOnServer(ServerPlayer entity, WeaponsAnvilGUIButtonMessage message) {
		handleButtonAction(entity, message.buttonID, message.x, message.y, message.z);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = WeaponsAnvilGUIMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			InsertGemsProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {
			RemoveGemProcedure.execute(entity);
		}
	}
}
