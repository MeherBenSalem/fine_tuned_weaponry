package io.nightbeam.runeborn.core.shrine;

import java.util.Locale;

/**
 * Generates evocative, stable names for shrines from their position and biome,
 * e.g. "Weeping Birchwood Hollow" or "Shrine of the Ashen Wastes".
 */
public final class ShrineNames {
    private static final String[] ADJECTIVES = {
            "Weeping", "Ashen", "Forsaken", "Gilded", "Hollow", "Withered", "Silent", "Shattered", "Ember-lit",
            "Moonlit", "Sunken", "Forgotten", "Crimson", "Pale", "Thorned", "Lonely", "Ruined", "Starfallen",
            "Gloaming", "Mournful", "Sacred", "Wind-torn", "Rime-crusted", "Sundered"
    };
    private static final String[] PLACES = {
            "Hollow", "Crossroads", "Watch", "Overlook", "Ruins", "Vigil", "Rest", "Cairn", "Steps", "Shore",
            "Gate", "Spire", "Wayshrine", "Bluff", "Ford", "Sanctum", "Chapel", "Approach"
    };

    public static String generate(String biomeId, String dimensionId, int x, int y, int z) {
        long h = mix(x * 73428767L ^ z * 912931L ^ y * 4231L ^ dimensionId.hashCode());
        String adjective = ADJECTIVES[Math.floorMod(h, ADJECTIVES.length)];
        String place = PLACES[Math.floorMod(h >>> 17, PLACES.length)];
        String biome = biomeWord(biomeId);
        if (Math.floorMod(h >>> 33, 4) == 0) {
            return "Shrine of the " + adjective + " " + pluralize(biome);
        }
        return adjective + " " + biome + " " + place;
    }

    static String biomeWord(String biomeId) {
        String path = biomeId.contains(":") ? biomeId.substring(biomeId.indexOf(':') + 1) : biomeId;
        if (path.contains("crimson")) return "Crimson";
        if (path.contains("warped")) return "Warped";
        if (path.contains("soul")) return "Soulsand";
        if (path.contains("basalt")) return "Basalt";
        if (path.contains("nether")) return "Cinder";
        if (path.startsWith("end") || path.contains("_end") || path.contains("void")) return "Void";
        if (path.contains("cherry")) return "Blossom";
        if (path.contains("birch")) return "Birchwood";
        if (path.contains("dark_forest")) return "Shadewood";
        if (path.contains("taiga") || path.contains("spruce")) return "Pinewood";
        if (path.contains("forest")) return "Greenwood";
        if (path.contains("jungle") || path.contains("bamboo")) return "Wildwood";
        if (path.contains("swamp") || path.contains("mangrove")) return "Mire";
        if (path.contains("desert")) return "Dune";
        if (path.contains("badlands") || path.contains("mesa")) return "Redrock";
        if (path.contains("savanna")) return "Steppe";
        if (path.contains("snow") || path.contains("frozen") || path.contains("ice")) return "Rime";
        if (path.contains("peak") || path.contains("mountain") || path.contains("slope") || path.contains("hill")) return "Highland";
        if (path.contains("ocean") || path.contains("beach") || path.contains("shore")) return "Tide";
        if (path.contains("river")) return "River";
        if (path.contains("mushroom")) return "Sporecap";
        if (path.contains("deep_dark")) return "Abyss";
        if (path.contains("cave") || path.contains("dripstone") || path.contains("lush")) return "Grotto";
        if (path.contains("meadow") || path.contains("plains") || path.contains("sunflower")) return "Meadow";
        String word = path.replace('_', ' ').trim();
        if (word.isEmpty()) {
            return "Wild";
        }
        int space = word.lastIndexOf(' ');
        word = space >= 0 ? word.substring(space + 1) : word;
        return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
    }

    private static String pluralize(String word) {
        return switch (word) {
            case "Mire", "Rime", "Tide", "Abyss", "Void", "Crimson", "Warped", "Basalt", "Soulsand", "Cinder" -> word;
            case "Dune" -> "Dunes";
            default -> word + "s";
        };
    }

    private static long mix(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    private ShrineNames() {
    }
}
