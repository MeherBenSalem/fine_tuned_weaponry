package io.nightbeam.runeborn.forge;

import io.nightbeam.runeborn.content.WeaponItem;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;

/** Forge sweeps whenever the held item can perform {@code SWORD_SWEEP}; Runeborn sweeps by weapon class instead. */
final class ForgeWeaponItem extends WeaponItem {
    ForgeWeaponItem(WeaponSpec spec, Properties properties) {
        super(spec, properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return false;
    }
}
