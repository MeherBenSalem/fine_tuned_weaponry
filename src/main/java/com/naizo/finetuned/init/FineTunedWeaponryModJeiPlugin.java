
package com.naizo.finetuned.init;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;

import java.util.Objects;
import java.util.List;

import com.naizo.finetuned.jei_recipes.WeaponForgeStationJEIRecipeCategory;
import com.naizo.finetuned.jei_recipes.WeaponForgeStationJEIRecipe;
import com.naizo.finetuned.jei_recipes.ResearchTableJEIRecipeCategory;
import com.naizo.finetuned.jei_recipes.ResearchTableJEIRecipe;

@JeiPlugin
public class FineTunedWeaponryModJeiPlugin implements IModPlugin {
	public static mezz.jei.api.recipe.RecipeType<WeaponForgeStationJEIRecipe> WeaponForgeStationJEI_Type = new mezz.jei.api.recipe.RecipeType<>(WeaponForgeStationJEIRecipeCategory.UID, WeaponForgeStationJEIRecipe.class);
	public static mezz.jei.api.recipe.RecipeType<ResearchTableJEIRecipe> ResearchTableJEI_Type = new mezz.jei.api.recipe.RecipeType<>(ResearchTableJEIRecipeCategory.UID, ResearchTableJEIRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("fine_tuned_weaponry:jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new WeaponForgeStationJEIRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new ResearchTableJEIRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		List<WeaponForgeStationJEIRecipe> WeaponForgeStationJEIRecipes = recipeManager.getAllRecipesFor(WeaponForgeStationJEIRecipe.Type.INSTANCE);
		registration.addRecipes(WeaponForgeStationJEI_Type, WeaponForgeStationJEIRecipes);
		List<ResearchTableJEIRecipe> ResearchTableJEIRecipes = recipeManager.getAllRecipesFor(ResearchTableJEIRecipe.Type.INSTANCE);
		registration.addRecipes(ResearchTableJEI_Type, ResearchTableJEIRecipes);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get().asItem()), WeaponForgeStationJEI_Type);
		registration.addRecipeCatalyst(new ItemStack(FineTunedWeaponryModBlocks.RESEARCH_TABLE.get().asItem()), ResearchTableJEI_Type);
	}
}
