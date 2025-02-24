
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import com.naizo.finetuned.block.entity.WeaponsForgeUnActiveBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity;
import com.naizo.finetuned.block.entity.ResearchTableBlockEntity;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> WEAPONS_FORGE_ACTIVE = register("weapons_forge_active", FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE, WeaponsForgeActiveBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> WEAPONS_FORGE_UN_ACTIVE = register("weapons_forge_un_active", FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE, WeaponsForgeUnActiveBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> RESEARCH_TABLE = register("research_table", FineTunedWeaponryModBlocks.RESEARCH_TABLE, ResearchTableBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> WEAPONS_ANVIL = register("weapons_anvil", FineTunedWeaponryModBlocks.WEAPONS_ANVIL, WeaponsAnvilBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
