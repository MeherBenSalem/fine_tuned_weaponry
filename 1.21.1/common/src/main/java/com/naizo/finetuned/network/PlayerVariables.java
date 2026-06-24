package com.naizo.finetuned.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import com.naizo.finetuned.platform.Services;

public final class PlayerVariables {
	public double page = 0;

	public void sync(Entity entity) {
		if (entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
			Services.NETWORK.sendToPlayer(serverPlayer, new SyncMessage(this));
		}
	}

	public Tag writeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putDouble("page", page);
		return nbt;
	}

	public void readNBT(Tag tag) {
		CompoundTag nbt = (CompoundTag) tag;
		page = nbt.getDouble("page");
	}

	public static class SyncMessage {
		private final PlayerVariables data;

		public SyncMessage(PlayerVariables data) {
			this.data = data;
		}

		public SyncMessage(FriendlyByteBuf buffer) {
			this.data = new PlayerVariables();
			this.data.readNBT(buffer.readNbt());
		}

		public static void encode(SyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeNbt((CompoundTag) message.data.writeNBT());
		}

		public static void handle(SyncMessage message, boolean isClientSide) {
			if (isClientSide) {
				PlayerVariables variables = Services.PLAYER_VARIABLES.get(net.minecraft.client.Minecraft.getInstance().player);
				variables.page = message.data.page;
			}
		}
	}
}
