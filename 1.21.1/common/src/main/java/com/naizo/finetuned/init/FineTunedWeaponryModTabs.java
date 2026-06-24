package com.naizo.finetuned.init;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class FineTunedWeaponryModTabs {
	public static final RegistryHolder<CreativeModeTab> FINE_TUNED_WEAPONS = new RegistryHolder<>("fine_tuned_weapons");
	public static final RegistryHolder<CreativeModeTab> FINE_TUNNED_MATS = new RegistryHolder<>("fine_tunned_mats");

	public static CreativeModeTab createWeaponsTab() {
		return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("item_group.fine_tuned_weaponry.fine_tuned_weapons"))
				.icon(() -> new ItemStack(FineTunedWeaponryModItems.CLASSIC_KATANA.get()))
				.displayItems((parameters, tabData) -> {
					tabData.accept(FineTunedWeaponryModItems.CLASSIC_KATANA.get());
					tabData.accept(FineTunedWeaponryModItems.BLOOD_KATANA.get());
					tabData.accept(FineTunedWeaponryModItems.INOSUKESSWORD.get());
					tabData.accept(FineTunedWeaponryModItems.MOONS_LUNAR_BLOOMFANG.get());
					tabData.accept(FineTunedWeaponryModItems.HELISH_SWORD.get());
					tabData.accept(FineTunedWeaponryModItems.IMMORTAL_SCYTHE.get());
					tabData.accept(FineTunedWeaponryModItems.ZENITSUS_SWORD.get());
					tabData.accept(FineTunedWeaponryModItems.BONE_SWORD.get());
					tabData.accept(FineTunedWeaponryModItems.HOLLOW_MAN_RUYI_JINGU_STAFF.get());
					tabData.accept(FineTunedWeaponryModItems.CLASSICHAMMER.get());
					tabData.accept(FineTunedWeaponryModItems.EARTHLYHAMMER.get());
					tabData.accept(FineTunedWeaponryModItems.OBSIDIANHAMMER.get());
					tabData.accept(FineTunedWeaponryModItems.ROSEGOLDHAMMER.get());
					tabData.accept(FineTunedWeaponryModItems.WARAXE.get());
				}).build();
	}

	public static CreativeModeTab createMatsTab() {
		return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
				.title(Component.translatable("item_group.fine_tuned_weaponry.fine_tunned_mats"))
				.icon(() -> new ItemStack(FineTunedWeaponryModItems.EARTHLY_INGOT.get()))
				.displayItems((parameters, tabData) -> {
					tabData.accept(FineTunedWeaponryModItems.WEAPONTEMPLATE.get());
					tabData.accept(FineTunedWeaponryModItems.EARTHLY_INGOT.get());
					tabData.accept(FineTunedWeaponryModItems.OBSIDIAN_FORGED_INGOT.get());
					tabData.accept(FineTunedWeaponryModItems.ROSE_GOLD_INGOT.get());
					tabData.accept(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get().asItem());
					tabData.accept(FineTunedWeaponryModBlocks.RESEARCH_TABLE.get().asItem());
					tabData.accept(FineTunedWeaponryModItems.INFERNO_CORE.get());
					tabData.accept(FineTunedWeaponryModItems.STORM_SHARD.get());
					tabData.accept(FineTunedWeaponryModItems.FROST_RUNE.get());
					tabData.accept(FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.STATIC_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER.get());
					tabData.accept(FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get().asItem());
				}).build();
	}

	private FineTunedWeaponryModTabs() {
	}
}
