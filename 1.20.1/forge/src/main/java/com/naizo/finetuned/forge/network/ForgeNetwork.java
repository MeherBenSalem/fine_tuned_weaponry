package com.naizo.finetuned.forge.network;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.*;
import com.naizo.finetuned.platform.services.INetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ForgeNetwork implements INetworkHelper {
	private static final String PROTOCOL = "1";
	private final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
			new net.minecraft.resources.ResourceLocation(Constants.MOD_ID, Constants.MOD_ID),
			() -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
	private int nextId = 0;
	private final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public ForgeNetwork() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
	}

	@Override
	public <T> void registerPacket(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, PacketHandler<T> handler) {
		channel.registerMessage(nextId++, type, encoder, decoder, (message, ctx) -> {
			NetworkEvent.Context context = ctx.get();
			context.enqueueWork(() -> {
				if (message instanceof WeaponsForgeGUIButtonMessage gui && context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
					WeaponsForgeGUIButtonMessage.handleOnServer(context.getSender(), gui);
				} else if (message instanceof WeaponsAnvilGUIButtonMessage gui && context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
					WeaponsAnvilGUIButtonMessage.handleOnServer(context.getSender(), gui);
				} else if (message instanceof ResearchTableGUIButtonMessage gui && context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
					ResearchTableGUIButtonMessage.handleOnServer(context.getSender(), gui);
				} else if (message instanceof PlayerVariables.SyncMessage sync) {
					PlayerVariables.SyncMessage.handle(sync, context.getDirection().getReceptionSide().isClient());
				} else {
					handler.handle(message, context.getDirection().getReceptionSide().isClient());
				}
			});
			context.setPacketHandled(true);
		});
	}

	@Override
	public void sendToPlayer(ServerPlayer player, Object message) {
		channel.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	@Override
	public void sendToServer(Object message) {
		channel.sendToServer(message);
	}

	@Override
	public void queueServerWork(int ticks, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry<>(action, ticks));
	}

	private void onServerTick(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
		if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
			return;
		}
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
