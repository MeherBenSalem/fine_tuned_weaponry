package com.naizo.finetuned.neoforge.network;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.*;
import com.naizo.finetuned.platform.services.INetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NeoForgeNetwork implements INetworkHelper {
	private static final String PROTOCOL = "1";
	private final Map<Class<?>, ResourceLocation> ids = new HashMap<>();
	private final Map<ResourceLocation, Function<FriendlyByteBuf, ?>> decoders = new HashMap<>();
	private final Map<ResourceLocation, PacketHandler<?>> handlers = new HashMap<>();
	private final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
	private int nextId = 0;

	public NeoForgeNetwork() {
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onServerTick);
	}

	public void registerPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PROTOCOL);
		registrar.playToServer(NeoForgeDynamicPayload.TYPE, NeoForgeDynamicPayload.STREAM_CODEC, this::handleServerPayload);
		registrar.playToClient(NeoForgeDynamicPayload.TYPE, NeoForgeDynamicPayload.STREAM_CODEC, this::handleClientPayload);
	}

	private void handleServerPayload(NeoForgeDynamicPayload payload, IPayloadContext context) {
		FriendlyByteBuf buf = payload.data();
		ResourceLocation packetId = buf.readResourceLocation();
		Function<FriendlyByteBuf, ?> decoder = decoders.get(packetId);
		if (decoder == null) {
			return;
		}
		Object message = decoder.apply(buf);
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer serverPlayer)) {
				return;
			}
			if (message instanceof WeaponsForgeGUIButtonMessage gui) {
				WeaponsForgeGUIButtonMessage.handleOnServer(serverPlayer, gui);
			} else if (message instanceof WeaponsAnvilGUIButtonMessage gui) {
				WeaponsAnvilGUIButtonMessage.handleOnServer(serverPlayer, gui);
			} else if (message instanceof ResearchTableGUIButtonMessage gui) {
				ResearchTableGUIButtonMessage.handleOnServer(serverPlayer, gui);
			}
		});
	}

	private void handleClientPayload(NeoForgeDynamicPayload payload, IPayloadContext context) {
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
		context.enqueueWork(() -> typedHandler.handle(message, true));
	}

	@Override
	public <T> void registerPacket(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "packet_" + (nextId++));
		ids.put(type, id);
		decoders.put(id, decoder);
		handlers.put(id, handler);
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
		net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new NeoForgeDynamicPayload(buf));
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
		net.neoforged.neoforge.network.PacketDistributor.sendToServer(new NeoForgeDynamicPayload(buf));
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

	private void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
		List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setValue(work.getValue() - 1);
			if (work.getValue() == 0) {
				actions.add(work);
			}
		});
		actions.forEach(e -> e.getKey().run());
		workQueue.removeAll(actions);
	}
}
