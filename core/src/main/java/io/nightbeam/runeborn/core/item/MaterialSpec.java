package io.nightbeam.runeborn.core.item;

import io.nightbeam.runeborn.core.weapon.Rarity;

import java.util.List;

/**
 * A non-weapon item. {@code value} is kind-specific: tier for stones, rune amount for runes,
 * affinity ordinal for whetstones.
 * {@code texture} is {@code <template>:<palette>} for the sprite generator.
 */
public record MaterialSpec(String id, String name, Kind kind, Rarity rarity, int stack, int value, String ref,
                           String texture, List<String> lore) {
    public enum Kind {
        TEMPERING_STONE, LUSTROUS_STONE, WHETSTONE, BLANK_WHETSTONE, RUNE, EMBER_FLASK, AZURE_FLASK, SUNSEED,
        HALLOWED_TEAR, REBIRTH_TEAR, SIGIL, RELIC, CROWN_SHARD, FRAGMENT
    }

    public String langKey() {
        return "item.runeborn." + id;
    }

    /** True for items with a right-click action. */
    public boolean usable() {
        return switch (kind) {
            case RUNE, EMBER_FLASK, AZURE_FLASK, SUNSEED, HALLOWED_TEAR, SIGIL, CROWN_SHARD -> true;
            default -> false;
        };
    }
}
