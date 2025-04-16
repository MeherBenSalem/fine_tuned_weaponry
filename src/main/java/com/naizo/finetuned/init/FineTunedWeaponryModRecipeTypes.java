package com.naizo.finetuned.init;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraft.world.item.crafting.RecipeSerializer;

import com.naizo.finetuned.jei_recipes.WeaponForgeStationJEIRecipe;
import com.naizo.finetuned.jei_recipes.ResearchTableJEIRecipe;
import com.naizo.finetuned.FineTunedWeaponryMod;

@Mod.EventBusSubscriber(modid = FineTunedWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FineTunedWeaponryModRecipeTypes {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "fine_tuned_weaponry");

	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		event.enqueueWork(() -> {
			SERIALIZERS.register(bus);
			SERIALIZERS.register("weapon_forge_station_jei", () -> WeaponForgeStationJEIRecipe.Serializer.INSTANCE);
			SERIALIZERS.register("research_table_jei", () -> ResearchTableJEIRecipe.Serializer.INSTANCE);
		});
	}
}
