package com.naizo.finetuned.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public final class RegistryHelper {
	private RegistryHelper() {
	}

	public static Item getItem(ResourceLocation id) {
		return BuiltInRegistries.ITEM.get(id);
	}

	public static Item getItem(String id) {
		return getItem(ResourceLocation.parse(id));
	}

	public static SoundEvent getSoundEvent(ResourceLocation id) {
		return BuiltInRegistries.SOUND_EVENT.get(id);
	}

	public static SoundEvent getSoundEvent(String id) {
		return getSoundEvent(ResourceLocation.parse(id));
	}
}
