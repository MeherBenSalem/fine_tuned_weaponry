package com.naizo.finetuned.neoforge.data;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.RESEARCH_TABLE.get())
				.pattern("PPP")
				.pattern(" B ")
				.pattern("PPP")
				.define('P', Items.OAK_PLANKS)
				.define('B', Items.BOOK)
				.unlockedBy("has_book", has(Items.BOOK))
				.save(output, Constants.MOD_ID + ":table_recipe");

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get())
				.pattern("III")
				.pattern(" A ")
				.pattern("III")
				.define('I', Items.IRON_INGOT)
				.define('A', Items.ANVIL)
				.unlockedBy("has_anvil", has(Items.ANVIL))
				.save(output, Constants.MOD_ID + ":weapons_anvil_recipe");

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get())
				.pattern("IBI")
				.pattern("B B")
				.pattern("IBI")
				.define('I', Items.IRON_INGOT)
				.define('B', Items.BRICKS)
				.unlockedBy("has_bricks", has(Items.BRICKS))
				.save(output, Constants.MOD_ID + ":forge_recipe");
	}
}
