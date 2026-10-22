package io.nightbeam.runeborn.core.boss;

import java.util.List;

/**
 * A Sovereign or field boss. Bosses are vanilla mobs ({@code entity}) dressed, scaled and driven by
 * scripted {@link Ability abilities}. Vanilla bosses use {@code summonable = false}.
 *
 * @param health   max health in vanilla points
 * @param damage   attack damage in vanilla points
 * @param relics   weapon ids offered when the relic is exchanged at a shrine
 */
public record BossSpec(String id, String name, String title, String entity, boolean summonable, String weapon,
                       double health, double damage, double armor, double speed, int level, long runes,
                       List<Ability> abilities, List<String> relics, CrownShard shard, int color, String sigilRecipe) {

    public String nameKey() {
        return "runeborn.boss." + id;
    }

    public String titleKey() {
        return "runeborn.boss." + id + ".title";
    }

    public String relicId() {
        return "relic_" + id;
    }

    public String sigilId() {
        return "sigil_" + id;
    }

    public String shardId() {
        return shard == null ? null : "crown_shard_" + id;
    }

    /** Entity tag marking a live boss of this kind. */
    public String tag() {
        return "runeborn.boss." + id;
    }
}
