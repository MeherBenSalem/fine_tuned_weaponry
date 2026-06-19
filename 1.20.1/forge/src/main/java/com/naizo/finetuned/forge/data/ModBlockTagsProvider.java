package com.naizo.finetuned.forge.data;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_AXE)
				.add(
						FineTunedWeaponryModBlocks.RESEARCH_TABLE.get(),
						FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get(),
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get(),
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get()
				);
		tag(BlockTags.NEEDS_STONE_TOOL)
				.add(
						FineTunedWeaponryModBlocks.RESEARCH_TABLE.get(),
						FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get(),
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get(),
						FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get()
				);
	}
}
