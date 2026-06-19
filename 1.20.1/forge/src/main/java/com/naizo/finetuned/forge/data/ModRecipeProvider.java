package com.naizo.finetuned.forge.data;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
	public ModRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.RESEARCH_TABLE.get())
				.pattern("PPP")
				.pattern(" B ")
				.pattern("PPP")
				.define('P', Items.OAK_PLANKS)
				.define('B', Items.BOOK)
				.unlockedBy("has_book", has(Items.BOOK))
				.save(consumer, Constants.MOD_ID + ":table_recipe");

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get())
				.pattern("III")
				.pattern(" A ")
				.pattern("III")
				.define('I', Items.IRON_INGOT)
				.define('A', Items.ANVIL)
				.unlockedBy("has_anvil", has(Items.ANVIL))
				.save(consumer, Constants.MOD_ID + ":weapons_anvil_recipe");

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get())
				.pattern("IBI")
				.pattern("B B")
				.pattern("IBI")
				.define('I', Items.IRON_INGOT)
				.define('B', Items.BRICKS)
				.unlockedBy("has_bricks", has(Items.BRICKS))
				.save(consumer, Constants.MOD_ID + ":forge_recipe");
	}
}
