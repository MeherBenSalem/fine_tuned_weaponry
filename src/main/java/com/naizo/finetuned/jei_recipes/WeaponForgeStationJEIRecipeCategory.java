
package com.naizo.finetuned.jei_recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.constants.VanillaTypes;

import com.naizo.finetuned.init.FineTunedWeaponryModJeiPlugin;
import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;

public class WeaponForgeStationJEIRecipeCategory implements IRecipeCategory<WeaponForgeStationJEIRecipe> {
	public final static ResourceLocation UID = new ResourceLocation("fine_tuned_weaponry", "weapon_forge_station_jei");
	public final static ResourceLocation TEXTURE = new ResourceLocation("fine_tuned_weaponry", "textures/screens/weaponsforge_jei.png");
	private final IDrawable background;
	private final IDrawable icon;

	public WeaponForgeStationJEIRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 79);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get().asItem()));
	}

	@Override
	public mezz.jei.api.recipe.RecipeType<WeaponForgeStationJEIRecipe> getRecipeType() {
		return FineTunedWeaponryModJeiPlugin.WeaponForgeStationJEI_Type;
	}

	@Override
	public Component getTitle() {
		return Component.literal("Weapon Forge");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, WeaponForgeStationJEIRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 19, 17).addIngredients(recipe.getIngredients().get(0));
		builder.addSlot(RecipeIngredientRole.INPUT, 44, 17).addIngredients(recipe.getIngredients().get(1));
		builder.addSlot(RecipeIngredientRole.INPUT, 31, 39).addIngredients(recipe.getIngredients().get(2));
		builder.addSlot(RecipeIngredientRole.INPUT, 81, 17).addIngredients(recipe.getIngredients().get(3));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 129, 16).addItemStack(recipe.getResultItem(null));
	}
}
