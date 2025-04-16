
package com.naizo.finetuned.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;

import java.util.List;

@JeiPlugin
public class FineTunedWeaponryModJeiInformation implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("fine_tuned_weaponry:information");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(List.of(new ItemStack(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get())), VanillaTypes.ITEM_STACK, Component.translatable("jei.fine_tuned_weaponry.weapon_forge_activate"));
	}
}
