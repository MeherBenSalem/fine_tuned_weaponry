package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.WeaponClass;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/** Reinforcement level and affinity stored on weapon stacks. */
public final class Stacks {
    public static final String UPGRADE = "upgrade";
    public static final String AFFINITY = "affinity";

    private static final Map<WeaponClass, Map<Long, WeaponSpec>> IMPLICIT = new EnumMap<>(WeaponClass.class);

    public static int upgrade(ItemStack stack) {
        return Math.max(0, Compat.stackInt(stack, UPGRADE, 0));
    }

    public static void setUpgrade(ItemStack stack, int value) {
        Compat.setStackInt(stack, UPGRADE, value);
    }

    public static Affinity affinity(ItemStack stack) {
        return Affinity.byId(Compat.stackString(stack, AFFINITY, "standard"));
    }

    public static void setAffinity(ItemStack stack, Affinity affinity) {
        Compat.setStackString(stack, AFFINITY, affinity.id());
    }

    /**
     * The weapon profile for any held stack: Runeborn weapons use their own spec; vanilla and
     * third-party melee weapons get an implicit profile so attributes still matter.
     */
    public static WeaponSpec profile(ItemStack stack) {
        WeaponSpec spec = RbItems.weapon(stack);
        if (spec != null) {
            return spec;
        }
        WeaponClass cls = Compat.vanillaWeaponClass(stack);
        if (cls == null) {
            return null;
        }
        double attack = Compat.attackDamage(stack);
        if (attack <= 1.0) {
            return null;
        }
        long key = Math.round(attack * 100);
        return IMPLICIT.computeIfAbsent(cls, c -> new java.util.HashMap<>())
                .computeIfAbsent(key, k -> Weapons.vanilla(cls, attack));
    }

    private Stacks() {
    }
}
