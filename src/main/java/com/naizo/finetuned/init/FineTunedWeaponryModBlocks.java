
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import com.naizo.finetuned.block.GemTableBlock;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Block> GEM_TABLE = REGISTRY.register("gem_table", () -> new GemTableBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
