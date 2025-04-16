
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

public class ResearchTableJEIRecipeCategory implements IRecipeCategory<ResearchTableJEIRecipe> {
	public final static ResourceLocation UID = new ResourceLocation("fine_tuned_weaponry", "research_table_jei");
	public final static ResourceLocation TEXTURE = new ResourceLocation("fine_tuned_weaponry", "textures/screens/bookcreate_jei.png");
	private final IDrawable background;
	private final IDrawable icon;

	public ResearchTableJEIRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEXTURE, 0, 0, 140, 64);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FineTunedWeaponryModBlocks.RESEARCH_TABLE.get().asItem()));
	}

	@Override
	public mezz.jei.api.recipe.RecipeType<ResearchTableJEIRecipe> getRecipeType() {
		return FineTunedWeaponryModJeiPlugin.ResearchTableJEI_Type;
	}

	@Override
	public Component getTitle() {
		return Component.literal("Research Table");
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
	public void setRecipe(IRecipeLayoutBuilder builder, ResearchTableJEIRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 21, 12).addIngredients(recipe.getIngredients().get(0));
		builder.addSlot(RecipeIngredientRole.INPUT, 21, 37).addIngredients(recipe.getIngredients().get(1));
		builder.addSlot(RecipeIngredientRole.INPUT, 48, 24).addIngredients(recipe.getIngredients().get(2));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 24).addItemStack(recipe.getResultItem(null));
	}
}
