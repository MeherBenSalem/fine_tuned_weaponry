package com.naizo.finetuned.jei_recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ResearchTableJEIRecipe implements Recipe<ThreeSlotRecipeInput> {
	private static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> INGREDIENTS_STREAM_CODEC = StreamCodec.of(
			(buf, ingredients) -> {
				for (int i = 0; i < 3; i++) {
					Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredients.get(i));
				}
			},
			buf -> {
				NonNullList<Ingredient> ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
				for (int i = 0; i < 3; i++) {
					ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
				}
				return ingredients;
			}
	);

	public static final MapCodec<ResearchTableJEIRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemStack.STRICT_CODEC.fieldOf("output").forGetter(ResearchTableJEIRecipe::result),
			Ingredient.CODEC.listOf(3, 3).fieldOf("ingredients").forGetter(ResearchTableJEIRecipe::getIngredients)
	).apply(instance, ResearchTableJEIRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTableJEIRecipe> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC,
			ResearchTableJEIRecipe::result,
			INGREDIENTS_STREAM_CODEC,
			ResearchTableJEIRecipe::getIngredients,
			ResearchTableJEIRecipe::new
	);

	private final ItemStack output;
	private final NonNullList<Ingredient> recipeItems;

	public ResearchTableJEIRecipe(ItemStack output, NonNullList<Ingredient> recipeItems) {
		this.output = output;
		this.recipeItems = recipeItems;
	}

	public ResearchTableJEIRecipe(ItemStack output, java.util.List<Ingredient> recipeItems) {
		this(output, toIngredientList(recipeItems));
	}

	private static NonNullList<Ingredient> toIngredientList(java.util.List<Ingredient> recipeItems) {
		NonNullList<Ingredient> ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
		for (int i = 0; i < Math.min(3, recipeItems.size()); i++) {
			ingredients.set(i, recipeItems.get(i));
		}
		return ingredients;
	}

	private ItemStack result() {
		return output;
	}

	@Override
	public boolean matches(ThreeSlotRecipeInput input, Level level) {
		return false;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipeItems;
	}

	@Override
	public ItemStack assemble(ThreeSlotRecipeInput input, HolderLookup.Provider registries) {
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return output.copy();
	}

	@Override
	public RecipeType<?> getType() {
		return Type.INSTANCE;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Type implements RecipeType<ResearchTableJEIRecipe> {
		private Type() {
		}

		public static final Type INSTANCE = new Type();
		public static final String ID = "research_table_jei";
	}

	public static class Serializer implements RecipeSerializer<ResearchTableJEIRecipe> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public MapCodec<ResearchTableJEIRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ResearchTableJEIRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
