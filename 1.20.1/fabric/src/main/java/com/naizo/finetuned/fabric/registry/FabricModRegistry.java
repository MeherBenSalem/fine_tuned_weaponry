package com.naizo.finetuned.fabric.registry;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.block.entity.ResearchTableBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeUnActiveBlockEntity;
import com.naizo.finetuned.init.*;
import com.naizo.finetuned.registry.RegistryHolder;
import com.naizo.finetuned.world.inventory.ResearchTableGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundEvent;
import com.naizo.finetuned.jei_recipes.ResearchTableJEIRecipe;
import com.naizo.finetuned.jei_recipes.WeaponForgeStationJEIRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class FabricModRegistry {
	private FabricModRegistry() {
	}

	public static void register() {
		registerBlock(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE);
		registerBlock(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE);
		registerBlock(FineTunedWeaponryModBlocks.RESEARCH_TABLE);
		registerBlock(FineTunedWeaponryModBlocks.WEAPONS_ANVIL);

		registerItem(FineTunedWeaponryModItems.CLASSIC_KATANA);
		registerItem(FineTunedWeaponryModItems.BLOOD_KATANA);
		registerItem(FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG);
		registerItem(FineTunedWeaponryModItems.HOLLOW_MAN_RUYI_JINGU_STAFF);
		registerItem(FineTunedWeaponryModItems.WEAPONTEMPLATE);
		registerItem(FineTunedWeaponryModItems.CLASSICHAMMER);
		registerItem(FineTunedWeaponryModItems.EARTHLYHAMMER);
		registerItem(FineTunedWeaponryModItems.OBSIDIANHAMMER);
		registerItem(FineTunedWeaponryModItems.ROSEGOLDHAMMER);
		registerItem(FineTunedWeaponryModItems.EARTHLY_INGOT);
		registerItem(FineTunedWeaponryModItems.OBSIDIAN_FORGED_INGOT);
		registerItem(FineTunedWeaponryModItems.ROSE_GOLD_INGOT);
		registerItem(FineTunedWeaponryModItems.HELISH_SWORD);
		registerItem(FineTunedWeaponryModItems.IMMORTAL_SCYTHE);
		registerItem(FineTunedWeaponryModItems.INOSUKESSWORD);
		registerItem(FineTunedWeaponryModItems.WARAXE);
		registerItem(FineTunedWeaponryModItems.ZENITSUS_SWORD);
		registerItem(FineTunedWeaponryModItems.BONE_SWORD);
		registerItem(FineTunedWeaponryModItems.WEAPONS_FORGE_ACTIVE);
		registerItem(FineTunedWeaponryModItems.WEAPONS_FORGE_UN_ACTIVE);
		registerItem(FineTunedWeaponryModItems.RESEARCH_TABLE);
		registerItem(FineTunedWeaponryModItems.INFERNO_CORE);
		registerItem(FineTunedWeaponryModItems.STORM_SHARD);
		registerItem(FineTunedWeaponryModItems.FROST_RUNE);
		registerItem(FineTunedWeaponryModItems.BLAZING_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.CHARGED_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.STATIC_AMPLIFIER);
		registerItem(FineTunedWeaponryModItems.WEAPONS_ANVIL);

		registerBe(FineTunedWeaponryModBlockEntities.WEAPONS_FORGE_ACTIVE,
				BlockEntityType.Builder.of(WeaponsForgeActiveBlockEntity::new, FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get()).build(null));
		registerBe(FineTunedWeaponryModBlockEntities.WEAPONS_FORGE_UN_ACTIVE,
				BlockEntityType.Builder.of(WeaponsForgeUnActiveBlockEntity::new, FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get()).build(null));
		registerBe(FineTunedWeaponryModBlockEntities.RESEARCH_TABLE,
				BlockEntityType.Builder.of(ResearchTableBlockEntity::new, FineTunedWeaponryModBlocks.RESEARCH_TABLE.get()).build(null));
		registerBe(FineTunedWeaponryModBlockEntities.WEAPONS_ANVIL,
				BlockEntityType.Builder.of(WeaponsAnvilBlockEntity::new, FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get()).build(null));

		registerSound(FineTunedWeaponryModSounds.ACTIVATION_SOUND_EFFECT);
		registerSound(FineTunedWeaponryModSounds.KATANA_DASH_SOUND);
		registerSound(FineTunedWeaponryModSounds.LOVE_EFFECT_SOUND);
		registerEnchantment(FineTunedWeaponryModEnchantments.SUPER_CHARGE_ENCHANT);

		registerTab(FineTunedWeaponryModTabs.FINE_TUNED_WEAPONS, FineTunedWeaponryModTabs.createWeaponsTab());
		registerTab(FineTunedWeaponryModTabs.FINE_TUNNED_MATS, FineTunedWeaponryModTabs.createMatsTab());

		registerMenu(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI, new ExtendedScreenHandlerType<>(WeaponsForgeGUIMenu::new));
		registerMenu(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI, new ExtendedScreenHandlerType<>(WeaponsAnvilGUIMenu::new));
		registerMenu(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI, new ExtendedScreenHandlerType<>(ResearchTableGUIMenu::new));

		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constants.MOD_ID, "weapon_forge_station_jei"), WeaponForgeStationJEIRecipe.Serializer.INSTANCE);
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constants.MOD_ID, "research_table_jei"), ResearchTableJEIRecipe.Serializer.INSTANCE);
	}

	private static void registerBlock(RegistryHolder<Block> holder) {
		holder.bind(Registry.register(BuiltInRegistries.BLOCK, holder.id(), holder.get()));
	}

	private static void registerItem(RegistryHolder<Item> holder) {
		holder.bind(Registry.register(BuiltInRegistries.ITEM, holder.id(), holder.get()));
	}

	private static <T extends BlockEntity> void registerBe(RegistryHolder<BlockEntityType<T>> holder, BlockEntityType<T> type) {
		holder.bind(Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, holder.id(), type));
	}

	private static void registerSound(RegistryHolder<SoundEvent> holder) {
		holder.bind(Registry.register(BuiltInRegistries.SOUND_EVENT, holder.id(), holder.get()));
	}

	private static void registerEnchantment(RegistryHolder<Enchantment> holder) {
		holder.bind(Registry.register(BuiltInRegistries.ENCHANTMENT, holder.id(), holder.get()));
	}

	private static void registerTab(RegistryHolder<CreativeModeTab> holder, CreativeModeTab tab) {
		holder.bind(Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, holder.id(), tab));
	}

	private static <T extends AbstractContainerMenu> void registerMenu(RegistryHolder<MenuType<T>> holder, MenuType<T> type) {
		holder.bind(Registry.register(BuiltInRegistries.MENU, holder.id(), type));
	}
}
