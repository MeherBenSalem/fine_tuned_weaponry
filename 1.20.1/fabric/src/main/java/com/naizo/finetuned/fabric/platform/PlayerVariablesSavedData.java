package com.naizo.finetuned.fabric.platform;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.PlayerVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class PlayerVariablesSavedData extends SavedData {
	private static final String DATA_NAME = Constants.MOD_ID + "_player_variables";
	private final Map<UUID, PlayerVariables> variables = new HashMap<>();

	static PlayerVariablesSavedData get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(PlayerVariablesSavedData::load, PlayerVariablesSavedData::new, DATA_NAME);
	}

	PlayerVariables getOrCreate(UUID playerId) {
		return variables.computeIfAbsent(playerId, id -> new PlayerVariables());
	}

	void set(UUID playerId, PlayerVariables vars) {
		variables.put(playerId, vars);
		setDirty();
	}

	void remove(UUID playerId) {
		if (variables.remove(playerId) != null) {
			setDirty();
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag list = new ListTag();
		variables.forEach((id, vars) -> {
			CompoundTag entry = new CompoundTag();
			entry.putUUID("player", id);
			entry.put("data", (CompoundTag) vars.writeNBT());
			list.add(entry);
		});
		tag.put("players", list);
		return tag;
	}

	private static PlayerVariablesSavedData load(CompoundTag tag) {
		PlayerVariablesSavedData data = new PlayerVariablesSavedData();
		ListTag list = tag.getList("players", Tag.TAG_COMPOUND);
		for (Tag entryTag : list) {
			CompoundTag entry = (CompoundTag) entryTag;
			PlayerVariables vars = new PlayerVariables();
			vars.readNBT(entry.getCompound("data"));
			data.variables.put(entry.getUUID("player"), vars);
		}
		return data;
	}
}
