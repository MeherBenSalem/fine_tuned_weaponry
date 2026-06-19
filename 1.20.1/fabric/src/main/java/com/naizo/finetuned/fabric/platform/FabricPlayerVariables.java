package com.naizo.finetuned.fabric.platform;

import com.naizo.finetuned.network.PlayerVariables;
import com.naizo.finetuned.platform.Services;
import com.naizo.finetuned.platform.services.IPlayerVariablesAccess;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FabricPlayerVariables implements IPlayerVariablesAccess {
	private final Map<UUID, PlayerVariables> sessionCache = new ConcurrentHashMap<>();

	public FabricPlayerVariables() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			PlayerVariables loaded = getSavedData(player).getOrCreate(player.getUUID());
			sessionCache.put(player.getUUID(), loaded);
			sync(player);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayer player = handler.player;
			PlayerVariables vars = sessionCache.remove(player.getUUID());
			if (vars != null) {
				getSavedData(player).set(player.getUUID(), vars);
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			copyOnClone(oldPlayer, newPlayer, !alive);
			sync(newPlayer);
		});
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> copyOnClone(oldPlayer, newPlayer, !alive));
	}

	@Override
	public PlayerVariables get(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			return sessionCache.computeIfAbsent(player.getUUID(), id -> getSavedData(serverPlayer).getOrCreate(id));
		}
		return sessionCache.computeIfAbsent(player.getUUID(), id -> new PlayerVariables());
	}

	@Override
	public void sync(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			PlayerVariables vars = get(player);
			getSavedData(serverPlayer).set(player.getUUID(), vars);
			Services.NETWORK.sendToPlayer(serverPlayer, new PlayerVariables.SyncMessage(vars));
		}
	}

	@Override
	public void copyOnClone(Player original, Player clone, boolean wasDeath) {
		PlayerVariables cloneVars = get(clone);
		cloneVars.page = get(original).page;
		if (clone instanceof ServerPlayer serverPlayer) {
			getSavedData(serverPlayer).set(clone.getUUID(), cloneVars);
		}
	}

	private static PlayerVariablesSavedData getSavedData(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		return PlayerVariablesSavedData.get(level);
	}
}
