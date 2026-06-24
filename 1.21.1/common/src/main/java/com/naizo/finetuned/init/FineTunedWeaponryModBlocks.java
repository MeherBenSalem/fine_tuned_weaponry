package com.naizo.finetuned.init;

import com.naizo.finetuned.block.ResearchTableBlock;
import com.naizo.finetuned.block.WeaponsAnvilBlock;
import com.naizo.finetuned.block.WeaponsForgeActiveBlock;
import com.naizo.finetuned.block.WeaponsForgeUnActiveBlock;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.world.level.block.Block;

public final class FineTunedWeaponryModBlocks {
	public static final RegistryHolder<Block> WEAPONS_FORGE_ACTIVE = new RegistryHolder<>("weapons_forge_active", WeaponsForgeActiveBlock::new);
	public static final RegistryHolder<Block> WEAPONS_FORGE_UN_ACTIVE = new RegistryHolder<>("weapons_forge_un_active", WeaponsForgeUnActiveBlock::new);
	public static final RegistryHolder<Block> RESEARCH_TABLE = new RegistryHolder<>("research_table", ResearchTableBlock::new);
	public static final RegistryHolder<Block> WEAPONS_ANVIL = new RegistryHolder<>("weapons_anvil", WeaponsAnvilBlock::new);

	private FineTunedWeaponryModBlocks() {
	}
}
