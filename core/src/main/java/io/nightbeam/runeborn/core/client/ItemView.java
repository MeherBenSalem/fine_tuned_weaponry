package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

/**
 * Snapshot of an inventory stack for UI code.
 *
 * @param weaponId Runeborn weapon id, or empty when the stack is not a Runeborn weapon
 */
public record ItemView(int slot, String itemId, String weaponId, int upgrade, Affinity affinity, int count,
                       String name) {
    public static final ItemView EMPTY = new ItemView(-1, "minecraft:air", "", 0, Affinity.STANDARD, 0, "");

    public boolean isEmpty() {
        return count <= 0 || "minecraft:air".equals(itemId);
    }

    public WeaponSpec weapon() {
        return weaponId.isEmpty() ? null : Weapons.get(weaponId);
    }
}
