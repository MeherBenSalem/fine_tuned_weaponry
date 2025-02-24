
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import com.naizo.finetuned.block.WeaponsForgeUnActiveBlock;
import com.naizo.finetuned.block.WeaponsForgeActiveBlock;
import com.naizo.finetuned.block.WeaponsAnvilBlock;
import com.naizo.finetuned.block.ResearchTableBlock;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Block> WEAPONS_FORGE_ACTIVE = REGISTRY.register("weapons_forge_active", () -> new WeaponsForgeActiveBlock());
	public static final RegistryObject<Block> WEAPONS_FORGE_UN_ACTIVE = REGISTRY.register("weapons_forge_un_active", () -> new WeaponsForgeUnActiveBlock());
	public static final RegistryObject<Block> RESEARCH_TABLE = REGISTRY.register("research_table", () -> new ResearchTableBlock());
	public static final RegistryObject<Block> WEAPONS_ANVIL = REGISTRY.register("weapons_anvil", () -> new WeaponsAnvilBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
