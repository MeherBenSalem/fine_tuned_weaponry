package com.naizo.finetuned.jei_recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ThreeSlotRecipeInput(ItemStack slot0, ItemStack slot1, ItemStack slot2) implements RecipeInput {
	public ThreeSlotRecipeInput() {
		this(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem(int index) {
		return switch (index) {
			case 0 -> slot0;
			case 1 -> slot1;
			case 2 -> slot2;
			default -> ItemStack.EMPTY;
		};
	}

	@Override
	public int size() {
		return 3;
	}
}
