package io.nightbeam.runeborn.core.item;

import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.nightbeam.runeborn.core.item.MaterialSpec.Kind.*;

/** Every non-weapon item in the mod. */
public final class Materials {
    public static final String[] ROMAN = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    private static final Map<String, MaterialSpec> BY_ID = new LinkedHashMap<>();
    private static final List<MaterialSpec> ALL;

    static {
        // Flasks and their reagents
        add("ember_flask", "Ember Flask", EMBER_FLASK, Rarity.RARE, 1, 0, "", "flask:ember",
                "Tears of the shrine flame. Restores health.", "Charges refill when you rest at a shrine.");
        add("azure_flask", "Azure Flask", AZURE_FLASK, Rarity.RARE, 1, 0, "", "flask:azure",
                "Cold blue tears. Restores focus (FP).", "Charges refill when you rest at a shrine.");
        add("sunseed", "Sunseed", SUNSEED, Rarity.EPIC, 16, 0, "", "seed:sun",
                "A seed of the old golden tree.", "Use to add one flask charge (max 14).");
        add("hallowed_tear", "Hallowed Tear", HALLOWED_TEAR, Rarity.EPIC, 16, 0, "", "tear:hallowed",
                "A crystallised tear shed by a dying sovereign.", "Use to strengthen your flasks (max +12).");
        add("rebirth_tear", "Tear of Rebirth", REBIRTH_TEAR, Rarity.EPIC, 16, 0, "", "tear:rebirth",
                "Lets you be reborn at a shrine,", "redistributing every attribute point.");

        // Runes (consumable currency)
        add("faded_rune", "Faded Rune", RUNE, Rarity.COMMON, 64, 200, "", "rune:faded", "Use to gain 200 runes.");
        add("lustrous_rune", "Lustrous Rune", RUNE, Rarity.RARE, 64, 1_000, "", "rune:lustrous", "Use to gain 1,000 runes.");
        add("radiant_rune", "Radiant Rune", RUNE, Rarity.RARE, 64, 4_000, "", "rune:radiant", "Use to gain 4,000 runes.");
        add("heros_rune", "Hero's Rune", RUNE, Rarity.EPIC, 64, 12_000, "", "rune:hero", "Use to gain 12,000 runes.");
        add("sovereign_rune", "Sovereign Rune", RUNE, Rarity.LEGENDARY, 64, 40_000, "", "rune:sovereign",
                "Use to gain 40,000 runes.");

        // Tempering stones (standard weapons, +1 to +25)
        for (int tier = 1; tier <= 8; tier++) {
            int lo = tier * 3 - 2;
            int hi = tier * 3;
            add("tempering_stone_" + tier, "Tempering Stone " + ROMAN[tier], TEMPERING_STONE,
                    tier <= 3 ? Rarity.COMMON : tier <= 6 ? Rarity.RARE : Rarity.EPIC, 64, tier, "",
                    "stone:temper" + tier, "Reinforces standard weapons from +" + (lo - 1) + " to +" + hi + ".");
        }
        add("sovereign_tempering_stone", "Sovereign Tempering Stone", TEMPERING_STONE, Rarity.LEGENDARY, 64, 9, "",
                "stone:temper9", "Reinforces a standard weapon to +25.");
        // Lustrous stones (unique weapons, +1 to +10)
        for (int tier = 1; tier <= 9; tier++) {
            add("lustrous_stone_" + tier, "Lustrous Stone " + ROMAN[tier], LUSTROUS_STONE,
                    tier <= 3 ? Rarity.RARE : tier <= 6 ? Rarity.EPIC : Rarity.LEGENDARY, 64, tier, "",
                    "stone:lustrous" + tier, "Reinforces a unique weapon to +" + tier + ".");
        }
        add("sovereign_lustrous_stone", "Sovereign Lustrous Stone", LUSTROUS_STONE, Rarity.LEGENDARY, 64, 10, "",
                "stone:lustrous10", "Reinforces a unique weapon to +10.");

        // Whetstones (affinities)
        add("blank_whetstone", "Blank Whetstone", BLANK_WHETSTONE, Rarity.COMMON, 64, 0, "", "whetstone:blank",
                "Combine with a reagent to craft an affinity whetstone.");
        for (Affinity affinity : Affinity.ALL) {
            if (affinity == Affinity.STANDARD) {
                continue;
            }
            String name = io.nightbeam.runeborn.core.art.Names.title(affinity.name());
            add(affinity.id() + "_whetstone", name + " Whetstone", WHETSTONE, Rarity.RARE, 1, affinity.ordinal(),
                    affinity.id(), "whetstone:" + affinity.id(), "Lets you infuse standard weapons with the "
                            + name + " affinity at a shrine.", "It is not consumed.");
        }

        // Boss items
        add("sovereign_fragment", "Sovereign Fragment", FRAGMENT, Rarity.RARE, 64, 0, "", "fragment:sovereign",
                "A splinter of the sundered crown.", "Dropped by champions. Used to craft sigils.");
        for (BossSpec boss : Bosses.all()) {
            if (boss.summonable()) {
                add(boss.sigilId(), "Sigil of " + shortName(boss), SIGIL, Rarity.EPIC, 16, 0, boss.id(),
                        "sigil:" + boss.id(), "Use to call forth " + boss.name() + ".", boss.title() + ".");
            }
            add(boss.relicId(), "Relic of " + shortName(boss), RELIC, Rarity.LEGENDARY, 16, 0, boss.id(),
                    "relic:" + boss.id(), "Exchange at a shrine for one of its armaments.");
            if (boss.shard() != null) {
                add(boss.shardId(), "Crown Shard of " + shortName(boss), CROWN_SHARD, Rarity.LEGENDARY, 1, 0, boss.id(),
                        "shard:" + boss.id(), boss.shard().description, "Use to claim it; equip it at a shrine.");
            }
        }
        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    /** "Varghast the Grafted" -> "Varghast", "The Dusk Rider" -> "the Dusk Rider". */
    public static String shortName(BossSpec boss) {
        String name = boss.name();
        if (name.startsWith("The ")) {
            return "the " + name.substring(4);
        }
        int cut = name.length();
        for (String sep : new String[]{",", " the "}) {
            int i = name.indexOf(sep);
            if (i > 0 && i < cut) {
                cut = i;
            }
        }
        return name.substring(0, cut);
    }

    private static void add(String id, String name, MaterialSpec.Kind kind, Rarity rarity, int stack, int value,
                            String ref, String texture, String... lore) {
        if (BY_ID.put(id, new MaterialSpec(id, name, kind, rarity, stack, value, ref, texture, List.of(lore))) != null) {
            throw new IllegalStateException("Duplicate material " + id);
        }
    }

    public static List<MaterialSpec> all() {
        return ALL;
    }

    public static MaterialSpec get(String id) {
        return BY_ID.get(id);
    }

    private Materials() {
    }
}
