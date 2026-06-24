package com.naizo.finetuned.fabric.network;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.*;
import com.naizo.finetuned.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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
	private final Map<ResourceLocation, Function<FriendlyByteBuf, ?>> decoders = new HashMap<>();
	private final Map<ResourceLocation, PacketHandler<?>> handlers = new HashMap<>();
	private final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
	private int nextId = 0;
	private boolean payloadsRegistered;

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

	private void ensurePayloadsRegistered() {
		if (payloadsRegistered) {
			return;
		}
		PayloadTypeRegistry.playC2S().register(FabricDynamicPayload.TYPE, FabricDynamicPayload.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(FabricDynamicPayload.TYPE, FabricDynamicPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(FabricDynamicPayload.TYPE, (payload, context) -> {
			FriendlyByteBuf buf = payload.data();
			ResourceLocation packetId = buf.readResourceLocation();
			Function<FriendlyByteBuf, ?> decoder = decoders.get(packetId);
			if (decoder == null) {
				return;
			}
			Object message = decoder.apply(buf);
			context.server().execute(() -> handleServer(message, context.player()));
		});
		if (net.fabricmc.api.EnvType.CLIENT.equals(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType())) {
			ClientPlayNetworking.registerGlobalReceiver(FabricDynamicPayload.TYPE, (payload, context) -> {
				FriendlyByteBuf buf = payload.data();
				ResourceLocation packetId = buf.readResourceLocation();
				Function<FriendlyByteBuf, ?> decoder = decoders.get(packetId);
				PacketHandler<?> handler = handlers.get(packetId);
				if (decoder == null || handler == null) {
					return;
				}
				Object message = decoder.apply(buf);
				@SuppressWarnings("unchecked")
				PacketHandler<Object> typedHandler = (PacketHandler<Object>) handler;
				context.client().execute(() -> typedHandler.handle(message, true));
			});
		}
		payloadsRegistered = true;
	}

	@Override
	public <T> void registerPacket(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler) {
		ensurePayloadsRegistered();
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "packet_" + (nextId++));
		ids.put(type, id);
		decoders.put(id, decoder);
		handlers.put(id, handler);
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
		FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		buf.writeResourceLocation(id);
		encodeMessage(message, buf);
		ServerPlayNetworking.send(player, new FabricDynamicPayload(buf));
	}

	@Override
	public void sendToServer(Object message) {
		ResourceLocation id = ids.get(message.getClass());
		if (id == null) {
			return;
		}
		FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		buf.writeResourceLocation(id);
		encodeMessage(message, buf);
		ClientPlayNetworking.send(new FabricDynamicPayload(buf));
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
