package com.naizo.finetuned.init;

import com.naizo.finetuned.block.entity.ResearchTableBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeActiveBlockEntity;
import com.naizo.finetuned.block.entity.WeaponsForgeUnActiveBlockEntity;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class FineTunedWeaponryModBlockEntities {
	public static final RegistryHolder<BlockEntityType<WeaponsForgeActiveBlockEntity>> WEAPONS_FORGE_ACTIVE =
			new RegistryHolder<>("weapons_forge_active");
	public static final RegistryHolder<BlockEntityType<WeaponsForgeUnActiveBlockEntity>> WEAPONS_FORGE_UN_ACTIVE =
			new RegistryHolder<>("weapons_forge_un_active");
	public static final RegistryHolder<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
			new RegistryHolder<>("research_table");
	public static final RegistryHolder<BlockEntityType<WeaponsAnvilBlockEntity>> WEAPONS_ANVIL =
			new RegistryHolder<>("weapons_anvil");

	private FineTunedWeaponryModBlockEntities() {
	}
}
