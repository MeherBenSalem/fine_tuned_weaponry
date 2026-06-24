package com.naizo.finetuned.neoforge.registry;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.block.entity.ResearchTableBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeUnActiveBlockEntity;
import com.naizo.finetuned.enchantment.SuperChargeEnchantments;
import com.naizo.finetuned.init.*;
import com.naizo.finetuned.jei_recipes.ResearchTableJEIRecipe;
import com.naizo.finetuned.jei_recipes.WeaponForgeStationJEIRecipe;
import com.naizo.finetuned.registry.RegistryHolder;
import com.naizo.finetuned.world.inventory.ResearchTableGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsAnvilGUIMenu;
import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeModRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Constants.MOD_ID);
	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, Constants.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Constants.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

	private NeoForgeModRegistry() {
	}

	public static void register(IEventBus modBus) {
		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		BLOCK_ENTITIES.register(modBus);
		SOUNDS.register(modBus);
		ENCHANTMENTS.register(modBus);
		TABS.register(modBus);
		MENUS.register(modBus);
		RECIPE_SERIALIZERS.register(modBus);

		bindBlock(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE);
		bindBlock(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE);
		bindBlock(FineTunedWeaponryModBlocks.RESEARCH_TABLE);
		bindBlock(FineTunedWeaponryModBlocks.WEAPONS_ANVIL);

		bindItem(FineTunedWeaponryModItems.CLASSIC_KATANA);
		bindItem(FineTunedWeaponryModItems.BLOOD_KATANA);
		bindItem(FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG);
		bindItem(FineTunedWeaponryModItems.HOLLOW_MAN_RUYI_JINGU_STAFF);
		bindItem(FineTunedWeaponryModItems.WEAPONTEMPLATE);
		bindItem(FineTunedWeaponryModItems.CLASSICHAMMER);
		bindItem(FineTunedWeaponryModItems.EARTHLYHAMMER);
		bindItem(FineTunedWeaponryModItems.OBSIDIANHAMMER);
		bindItem(FineTunedWeaponryModItems.ROSEGOLDHAMMER);
		bindItem(FineTunedWeaponryModItems.EARTHLY_INGOT);
		bindItem(FineTunedWeaponryModItems.OBSIDIAN_FORGED_INGOT);
		bindItem(FineTunedWeaponryModItems.ROSE_GOLD_INGOT);
		bindItem(FineTunedWeaponryModItems.HELISH_SWORD);
		bindItem(FineTunedWeaponryModItems.IMMORTAL_SCYTHE);
		bindItem(FineTunedWeaponryModItems.INOSUKESSWORD);
		bindItem(FineTunedWeaponryModItems.WARAXE);
		bindItem(FineTunedWeaponryModItems.ZENITSUS_SWORD);
		bindItem(FineTunedWeaponryModItems.BONE_SWORD);
		bindItem(FineTunedWeaponryModItems.WEAPONS_FORGE_ACTIVE);
		bindItem(FineTunedWeaponryModItems.WEAPONS_FORGE_UN_ACTIVE);
		bindItem(FineTunedWeaponryModItems.RESEARCH_TABLE);
		bindItem(FineTunedWeaponryModItems.INFERNO_CORE);
		bindItem(FineTunedWeaponryModItems.STORM_SHARD);
		bindItem(FineTunedWeaponryModItems.FROST_RUNE);
		bindItem(FineTunedWeaponryModItems.BLAZING_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.CHARGED_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.STATIC_AMPLIFIER);
		bindItem(FineTunedWeaponryModItems.WEAPONS_ANVIL);

		bindBe(FineTunedWeaponryModBlockEntities.WEAPONS_FORGE_ACTIVE,
				() -> BlockEntityType.Builder.of(WeaponsForgeActiveBlockEntity::new,
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get()).build(null));
		bindBe(FineTunedWeaponryModBlockEntities.WEAPONS_FORGE_UN_ACTIVE,
				() -> BlockEntityType.Builder.of(WeaponsForgeUnActiveBlockEntity::new,
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get()).build(null));
		bindBe(FineTunedWeaponryModBlockEntities.RESEARCH_TABLE,
				() -> BlockEntityType.Builder.of(ResearchTableBlockEntity::new,
						FineTunedWeaponryModBlocks.RESEARCH_TABLE.get()).build(null));
		bindBe(FineTunedWeaponryModBlockEntities.WEAPONS_ANVIL,
				() -> BlockEntityType.Builder.of(WeaponsAnvilBlockEntity::new,
						FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get()).build(null));

		bindSound(FineTunedWeaponryModSounds.ACTIVATION_SOUND_EFFECT);
		bindSound(FineTunedWeaponryModSounds.KATANA_DASH_SOUND);
		bindSound(FineTunedWeaponryModSounds.LOVE_EFFECT_SOUND);

		bindEnchantment(FineTunedWeaponryModEnchantments.SUPER_CHARGE_ENCHANT);

		bindTab(FineTunedWeaponryModTabs.FINE_TUNED_WEAPONS, FineTunedWeaponryModTabs::createWeaponsTab);
		bindTab(FineTunedWeaponryModTabs.FINE_TUNNED_MATS, FineTunedWeaponryModTabs::createMatsTab);

		bindMenu(FineTunedWeaponryModMenus.WEAPONS_FORGE_GUI, () -> IMenuTypeExtension.create(WeaponsForgeGUIMenu::new));
		bindMenu(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI, () -> IMenuTypeExtension.create(WeaponsAnvilGUIMenu::new));
		bindMenu(FineTunedWeaponryModMenus.RESEARCH_TABLE_GUI, () -> IMenuTypeExtension.create(ResearchTableGUIMenu::new));

		RECIPE_SERIALIZERS.register("weapon_forge_station_jei", () -> WeaponForgeStationJEIRecipe.Serializer.INSTANCE);
		RECIPE_SERIALIZERS.register("research_table_jei", () -> ResearchTableJEIRecipe.Serializer.INSTANCE);
	}

	private static void bindBlock(RegistryHolder<Block> holder) {
		holder.bind(BLOCKS.register(holder.path(), holder::get).get());
	}

	private static void bindItem(RegistryHolder<Item> holder) {
		holder.bind(ITEMS.register(holder.path(), holder::get).get());
	}

	private static <T extends BlockEntity> void bindBe(RegistryHolder<BlockEntityType<T>> holder, java.util.function.Supplier<BlockEntityType<T>> factory) {
		holder.bind(BLOCK_ENTITIES.register(holder.path(), factory).get());
	}

	private static void bindSound(RegistryHolder<SoundEvent> holder) {
		holder.bind(SOUNDS.register(holder.path(), holder::get).get());
	}

	private static void bindEnchantment(RegistryHolder<Enchantment> holder) {
		DeferredHolder<Enchantment, Enchantment> registered = ENCHANTMENTS.register(holder.path(),
				() -> SuperChargeEnchantments.create(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
		holder.bind(registered.get());
	}

	private static void bindTab(RegistryHolder<CreativeModeTab> holder, java.util.function.Supplier<CreativeModeTab> factory) {
		holder.bind(TABS.register(holder.path(), factory).get());
	}

	private static <T extends AbstractContainerMenu> void bindMenu(RegistryHolder<MenuType<T>> holder, java.util.function.Supplier<MenuType<T>> factory) {
		holder.bind(MENUS.register(holder.path(), factory).get());
	}
}
