package com.naizo.finetuned.forge.data;

import com.naizo.finetuned.init.FineTunedWeaponryModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {
	public ModLootTableProvider(PackOutput output) {
		super(output, Set.of(), List.of(new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)));
	}

	private static class ModBlockLoot extends BlockLootSubProvider {
		protected ModBlockLoot() {
			super(Set.of(), FeatureFlags.DEFAULT_FLAGS);
		}

		@Override
		protected void generate() {
			dropSelf(FineTunedWeaponryModBlocks.RESEARCH_TABLE.get());
			dropSelf(FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get());
			dropSelf(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get());
			dropSelf(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return List.of(
					FineTunedWeaponryModBlocks.RESEARCH_TABLE.get(),
					FineTunedWeaponryModBlocks.WEAPONS_ANVIL.get(),
					FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE.get(),
					FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE.get()
			);
		}
	}
}
