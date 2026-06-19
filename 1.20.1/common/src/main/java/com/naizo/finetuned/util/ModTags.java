package com.naizo.finetuned.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
	public static final TagKey<Item> FORGE_TOOLS = itemTag("forge:tools");
	public static final TagKey<Item> GEM = itemTag("fine_tuned_weaponry:gem");
	public static final TagKey<Item> AMP = itemTag("fine_tuned_weaponry:amp");
	public static final TagKey<Item> TOOLS = itemTag("tools");
	public static final TagKey<Item> HAMMER = itemTag("minecraft:hammer");

	private ModTags() {
	}

	public static TagKey<Item> itemTag(String id) {
		return TagKey.create(Registries.ITEM, ResourceLocation.tryParse(id));
	}
}
