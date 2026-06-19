package com.naizo.finetuned.forge.data;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import com.naizo.finetuned.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
	public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModTags.GEM)
				.add(
						FineTunedWeaponryModItems.INFERNO_CORE.get(),
						FineTunedWeaponryModItems.STORM_SHARD.get(),
						FineTunedWeaponryModItems.FROST_RUNE.get()
				);
		tag(ModTags.AMP)
				.add(
						FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get(),
						FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get(),
						FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get(),
						FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get(),
						FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER.get(),
						FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER.get(),
						FineTunedWeaponryModItems.GLACIAL_AMPLIFIER.get(),
						FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER.get(),
						FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER.get(),
						FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get(),
						FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get(),
						FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get(),
						FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get(),
						FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get(),
						FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER.get(),
						FineTunedWeaponryModItems.STATIC_AMPLIFIER.get()
				);
		tag(ItemTags.SWORDS)
				.add(
						FineTunedWeaponryModItems.CLASSIC_KATANA.get(),
						FineTunedWeaponryModItems.BLOOD_KATANA.get(),
						FineTunedWeaponryModItems.HELISH_SWORD.get(),
						FineTunedWeaponryModItems.INOSUKESSWORD.get(),
						FineTunedWeaponryModItems.ZENITSUS_SWORD.get(),
						FineTunedWeaponryModItems.BONE_SWORD.get()
				);
	}
}
