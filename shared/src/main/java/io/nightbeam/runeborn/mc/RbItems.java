package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lookup tables for registered Runeborn items. Each version's registration code fills these, so
 * shared logic can go from an {@link ItemStack} to its weapon or material definition.
 */
public final class RbItems {
    private static final Map<String, Item> BY_PATH = new LinkedHashMap<>();
    private static final Map<Item, String> PATHS = new IdentityHashMap<>();
    private static final Map<Item, WeaponSpec> WEAPONS = new IdentityHashMap<>();
    private static final Map<Item, MaterialSpec> MATERIALS = new IdentityHashMap<>();
    public static Block shrineBlock;
    public static Item shrineItem;

    public static void add(String path, Item item) {
        BY_PATH.put(path, item);
        PATHS.put(item, path);
    }

    public static void addWeapon(WeaponSpec spec, Item item) {
        add(spec.id, item);
        WEAPONS.put(item, spec);
    }

    public static void addMaterial(MaterialSpec spec, Item item) {
        add(spec.id(), item);
        MATERIALS.put(item, spec);
    }

    public static Item get(String path) {
        return BY_PATH.get(path);
    }

    public static Map<String, Item> all() {
        return Collections.unmodifiableMap(BY_PATH);
    }

    public static ItemStack stack(String path, int count) {
        Item item = BY_PATH.get(path);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    public static String path(ItemStack stack) {
        return stack.isEmpty() ? null : PATHS.get(stack.getItem());
    }

    public static boolean is(ItemStack stack, String path) {
        return !stack.isEmpty() && path.equals(PATHS.get(stack.getItem()));
    }

    public static WeaponSpec weapon(ItemStack stack) {
        return stack.isEmpty() ? null : WEAPONS.get(stack.getItem());
    }

    public static MaterialSpec material(ItemStack stack) {
        return stack.isEmpty() ? null : MATERIALS.get(stack.getItem());
    }

    private RbItems() {
    }
}
