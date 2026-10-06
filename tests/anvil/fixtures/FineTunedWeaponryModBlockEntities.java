package com.naizo.finetuned.init;

import com.naizo.finetuned.registry.RegistryHolder;
import com.naizo.finetuned.block.entity.WeaponsAnvilBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Test registry binding only; the production block-entity implementation is used. */
public final class FineTunedWeaponryModBlockEntities {
    public static final RegistryHolder<BlockEntityType<WeaponsAnvilBlockEntity>> WEAPONS_ANVIL =
            new RegistryHolder<>("weapons_anvil");
}
