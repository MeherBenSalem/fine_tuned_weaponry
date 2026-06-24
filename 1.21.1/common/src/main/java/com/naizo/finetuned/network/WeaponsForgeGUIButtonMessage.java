package com.naizo.finetuned.network;

import com.naizo.finetuned.procedures.OnCraftForgeRecipesProcedure;
import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class WeaponsForgeGUIButtonMessage {
	private final int buttonID, x, y, z;

	public WeaponsForgeGUIButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public WeaponsForgeGUIButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(WeaponsForgeGUIButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handlePacket(WeaponsForgeGUIButtonMessage message, boolean isClientSide) {
		if (!isClientSide) {
			// handled on server via platform network implementation
		}
	}

	public static void handleOnServer(ServerPlayer entity, WeaponsForgeGUIButtonMessage message) {
		handleButtonAction(entity, message.buttonID, message.x, message.y, message.z);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = WeaponsForgeGUIMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			OnCraftForgeRecipesProcedure.execute(world, x, y, z, entity);
		}
	}
}
