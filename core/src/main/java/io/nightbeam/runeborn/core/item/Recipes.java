package io.nightbeam.runeborn.core.item;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Crafting recipes. The asset generator writes the data pack JSON from this list, and the Codex
 * shows the same recipes in game, so they cannot drift apart.
 */
public final class Recipes {
    /** Shaped when {@code pattern} is non-empty, otherwise shapeless over {@code ingredients}. */
    public record Recipe(String result, int count, List<String> pattern, Map<Character, String> key,
                         List<String> ingredients) {
        public boolean shaped() {
            return !pattern.isEmpty();
        }

        /** File name of the recipe JSON. */
        public String id() {
            return result.substring(result.indexOf(':') + 1);
        }

        /** The 3x3 crafting grid, row-major, with null for empty cells. */
        public String[] grid() {
            String[] cells = new String[9];
            if (shaped()) {
                for (int r = 0; r < pattern.size(); r++) {
                    String row = pattern.get(r);
                    for (int col = 0; col < row.length(); col++) {
                        char ch = row.charAt(col);
                        cells[r * 3 + col] = ch == ' ' ? null : key.get(ch);
                    }
                }
            } else {
                for (int i = 0; i < ingredients.size() && i < 9; i++) {
                    cells[i] = ingredients.get(i);
                }
            }
            return cells;
        }
    }

    public static final Map<String, List<String>> WHETSTONE_REAGENTS = new LinkedHashMap<>();
    private static final List<Recipe> ALL = new ArrayList<>();
    private static final Map<String, Recipe> BY_RESULT = new LinkedHashMap<>();

    static {
        reagents("heavy", "minecraft:iron_block");
        reagents("keen", "minecraft:flint", "minecraft:feather");
        reagents("quality", "minecraft:iron_ingot", "minecraft:gold_ingot");
        reagents("magic", "minecraft:lapis_block");
        reagents("fire", "minecraft:blaze_powder", "minecraft:magma_cream");
        reagents("flame_art", "minecraft:blaze_rod", "minecraft:gold_ingot");
        reagents("lightning", "minecraft:lightning_rod");
        reagents("sacred", "minecraft:golden_apple");
        reagents("cold", "minecraft:blue_ice");
        reagents("blood", "minecraft:redstone_block", "minecraft:nether_wart");
        reagents("poison", "minecraft:spider_eye", "minecraft:fermented_spider_eye");
        reagents("occult", "minecraft:echo_shard");

        shaped("ember_shrine", 1, List.of(" F ", "GBG", "SSS"), "F", "minecraft:blaze_powder",
                "G", "minecraft:gold_ingot", "B", "minecraft:campfire", "S", "minecraft:polished_deepslate");
        shapeless("ember_flask", 1, "minecraft:glass_bottle", "minecraft:blaze_powder", "minecraft:glistering_melon_slice");
        shapeless("azure_flask", 1, "minecraft:glass_bottle", "minecraft:lapis_lazuli", "minecraft:amethyst_shard");
        shapeless("tempering_stone_1", 2, "minecraft:iron_ingot", "minecraft:flint", "minecraft:cobbled_deepslate");
        shaped("blank_whetstone", 1, List.of("SFS"), "S", "minecraft:smooth_stone_slab", "F", "minecraft:flint");
        WHETSTONE_REAGENTS.forEach((affinity, reagents) -> {
            List<String> items = new ArrayList<>();
            items.add(Rb.id("blank_whetstone"));
            items.addAll(reagents);
            shapeless(affinity + "_whetstone", 1, items.toArray(new String[0]));
        });
        for (BossSpec boss : Bosses.all()) {
            if (boss.summonable() && !boss.sigilRecipe().isEmpty()) {
                String[] items = new String[7];
                for (int i = 0; i < 6; i++) {
                    items[i] = Rb.id("sovereign_fragment");
                }
                items[6] = boss.sigilRecipe();
                shapeless(boss.sigilId(), 1, items);
            }
        }
    }

    private static void reagents(String affinity, String... items) {
        WHETSTONE_REAGENTS.put(affinity, List.of(items));
    }

    /** {@code keys} alternates symbol and item id, in the order they appear in the JSON. */
    private static void shaped(String result, int count, List<String> pattern, String... keys) {
        Map<Character, String> key = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keys.length; i += 2) {
            key.put(keys[i].charAt(0), keys[i + 1]);
        }
        add(new Recipe(Rb.id(result), count, pattern, Collections.unmodifiableMap(key), List.of()));
    }

    private static void shapeless(String result, int count, String... items) {
        add(new Recipe(Rb.id(result), count, List.of(), Map.of(), List.of(items)));
    }

    private static void add(Recipe recipe) {
        ALL.add(recipe);
        BY_RESULT.put(recipe.result(), recipe);
    }

    public static List<Recipe> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** The recipe crafting {@code itemId} ({@code runeborn:foo}), or null. */
    public static Recipe forResult(String itemId) {
        return BY_RESULT.get(itemId);
    }

    private Recipes() {
    }
}
