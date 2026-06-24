package com.naizo.finetuned.platform.services;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface INetworkHelper {
	<T> void registerPacket(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler);

	void sendToPlayer(ServerPlayer player, Object message);

	void sendToServer(Object message);

	void queueServerWork(int ticks, Runnable action);

	@FunctionalInterface
	interface PacketHandler<T> {
		void handle(T message, boolean isClientSide);
	}
}
