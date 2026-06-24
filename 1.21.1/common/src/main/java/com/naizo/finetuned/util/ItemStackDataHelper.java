package com.naizo.finetuned.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ItemStackDataHelper {
	private ItemStackDataHelper() {
	}

	public static CompoundTag getOrCreateTag(ItemStack stack) {
		CustomData custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = custom.copyTag();
		if (!stack.has(DataComponents.CUSTOM_DATA)) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
		return tag;
	}

	public static CompoundTag getTag(ItemStack stack) {
		CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
		return custom != null ? custom.copyTag() : null;
	}

	public static void setTag(ItemStack stack, CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			stack.remove(DataComponents.CUSTOM_DATA);
		} else {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
	}

	public static void updateTag(ItemStack stack, java.util.function.Consumer<CompoundTag> mutator) {
		CompoundTag tag = getOrCreateTag(stack);
		mutator.accept(tag);
		setTag(stack, tag);
	}
}
