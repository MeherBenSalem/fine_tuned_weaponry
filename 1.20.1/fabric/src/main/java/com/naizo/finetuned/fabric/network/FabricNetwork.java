package com.naizo.finetuned.fabric.network;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.*;
import com.naizo.finetuned.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FabricNetwork implements INetworkHelper {
	private final Map<Class<?>, ResourceLocation> ids = new HashMap<>();
	private final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
	private int nextId = 0;

	public FabricNetwork() {
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0) {
					actions.add(work);
				}
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		});
	}

	@Override
	public <T> void registerPacket(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler) {
		ResourceLocation id = new ResourceLocation(Constants.MOD_ID, "packet_" + (nextId++));
		ids.put(type, id);
		ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler2, buf, responseSender) -> {
			T message = decoder.apply(buf);
			server.execute(() -> handleServer(message, player));
		});
		if (net.fabricmc.api.EnvType.CLIENT.equals(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType())) {
			registerClient(id, decoder, handler);
		}
	}

	private <T> void registerClient(ResourceLocation id, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler) {
		ClientPlayNetworking.registerGlobalReceiver(id, (client, handler2, buf, responseSender) -> {
			T message = decoder.apply(buf);
			client.execute(() -> handler.handle(message, true));
		});
	}

	private void handleServer(Object message, ServerPlayer player) {
		if (message instanceof WeaponsForgeGUIButtonMessage gui) {
			WeaponsForgeGUIButtonMessage.handleOnServer(player, gui);
		} else if (message instanceof WeaponsAnvilGUIButtonMessage gui) {
			WeaponsAnvilGUIButtonMessage.handleOnServer(player, gui);
		} else if (message instanceof ResearchTableGUIButtonMessage gui) {
			ResearchTableGUIButtonMessage.handleOnServer(player, gui);
		}
	}

	@Override
	public void sendToPlayer(ServerPlayer player, Object message) {
		ResourceLocation id = ids.get(message.getClass());
		if (id == null) {
			return;
		}
		FriendlyByteBuf buf = PacketByteBufs.create();
		if (message instanceof PlayerVariables.SyncMessage sync) {
			PlayerVariables.SyncMessage.encode(sync, buf);
		}
		ServerPlayNetworking.send(player, id, buf);
	}

	@Override
	public void sendToServer(Object message) {
		ResourceLocation id = ids.get(message.getClass());
		if (id == null) {
			return;
		}
		FriendlyByteBuf buf = PacketByteBufs.create();
		encodeMessage(message, buf);
		ClientPlayNetworking.send(id, buf);
	}

	private void encodeMessage(Object message, FriendlyByteBuf buf) {
		if (message instanceof WeaponsForgeGUIButtonMessage gui) {
			WeaponsForgeGUIButtonMessage.buffer(gui, buf);
		} else if (message instanceof WeaponsAnvilGUIButtonMessage gui) {
			WeaponsAnvilGUIButtonMessage.buffer(gui, buf);
		} else if (message instanceof ResearchTableGUIButtonMessage gui) {
			ResearchTableGUIButtonMessage.buffer(gui, buf);
		} else if (message instanceof PlayerVariables.SyncMessage sync) {
			PlayerVariables.SyncMessage.encode(sync, buf);
		}
	}

	@Override
	public void queueServerWork(int ticks, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry<>(action, ticks));
	}
}
