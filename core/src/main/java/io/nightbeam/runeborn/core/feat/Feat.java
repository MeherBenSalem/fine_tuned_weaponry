package io.nightbeam.runeborn.core.feat;

import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.boss.CrownShard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Milestones shown as a Runeborn advancement tab. The asset generator writes one advancement per
 * feat (with an impossible criterion) and the server grants them when they happen.
 */
public final class Feat {
    public enum Frame {
        TASK, GOAL, CHALLENGE
    }

    public static final String ROOT = "root";
    public static final String KINDLE = "kindle";
    public static final String TRAVEL = "travel";
    public static final String REBIRTH = "rebirth";
    public static final String LEVEL_20 = "level_20";
    public static final String LEVEL_50 = "level_50";
    public static final String LEVEL_100 = "level_100";
    public static final String INFUSED = "infused";
    public static final String TEMPERED = "tempered";
    public static final String MASTERWORK = "masterwork";
    public static final String CHAMPION = "champion";
    public static final String DIED = "died";
    public static final String RECLAIMED = "reclaimed";
    public static final String SUMMON = "summon";
    public static final String CROWN = "crown";

    /** Rune level thresholds, paired with {@link #LEVEL_FEATS}. */
    public static final int[] LEVEL_THRESHOLDS = {20, 50, 100};
    public static final String[] LEVEL_FEATS = {LEVEL_20, LEVEL_50, LEVEL_100};

    public final String id;
    public final String parent;
    public final String icon;
    public final Frame frame;
    public final String title;
    public final String description;
    public final boolean hidden;

    private Feat(String id, String parent, String icon, Frame frame, String title, String description, boolean hidden) {
        this.id = id;
        this.parent = parent;
        this.icon = icon;
        this.frame = frame;
        this.title = title;
        this.description = description;
        this.hidden = hidden;
    }

    public String titleKey() {
        return "runeborn.feat." + id;
    }

    public String descKey() {
        return "runeborn.feat." + id + ".desc";
    }

    /** Advancement path under the {@code runeborn} namespace. */
    public String path() {
        return "feats/" + id;
    }

    public static String fell(BossSpec boss) {
        return "fell_" + boss.id();
    }

    private static final Map<String, Feat> ALL = new LinkedHashMap<>();

    private static void add(String id, String parent, String icon, Frame frame, String title, String description) {
        ALL.put(id, new Feat(id, parent, icon, frame, title, description, false));
    }

    private static void hidden(String id, String parent, String icon, Frame frame, String title, String description) {
        ALL.put(id, new Feat(id, parent, icon, frame, title, description, true));
    }

    static {
        add(ROOT, null, "runeborn:ember_shrine", Frame.TASK, "Runeborn", "Rise, Runeborn, and choose who you were.");
        add(KINDLE, ROOT, "runeborn:ember_shrine", Frame.TASK, "Ember in the Dark", "Kindle an Ember Shrine.");
        add(TRAVEL, KINDLE, "minecraft:ender_pearl", Frame.TASK, "Paths of Ember", "Travel between kindled shrines.");
        add(REBIRTH, KINDLE, "runeborn:rebirth_tear", Frame.TASK, "Born Anew", "Reshape yourself with a Tear of Rebirth.");
        add(LEVEL_20, ROOT, "runeborn:faded_rune", Frame.TASK, "Rune-Touched", "Reach rune level 20.");
        add(LEVEL_50, LEVEL_20, "runeborn:radiant_rune", Frame.GOAL, "Runebearer", "Reach rune level 50.");
        add(LEVEL_100, LEVEL_50, "runeborn:sovereign_rune", Frame.CHALLENGE, "Ascendant", "Reach rune level 100.");
        add(INFUSED, KINDLE, "runeborn:fire_whetstone", Frame.TASK, "A Keener Edge", "Infuse an armament with an affinity.");
        add(TEMPERED, KINDLE, "runeborn:tempering_stone_4", Frame.TASK, "Tempered Steel",
                "Reinforce an armament to +10, or a legendary one to +4.");
        add(MASTERWORK, TEMPERED, "runeborn:sovereign_tempering_stone", Frame.CHALLENGE, "Masterwork",
                "Fully reinforce an armament.");
        hidden(DIED, ROOT, "minecraft:skeleton_skull", Frame.TASK, "You Died", "Fall for the first time. Not the last.");
        add(RECLAIMED, DIED, "runeborn:lustrous_rune", Frame.TASK, "Not This Time", "Reclaim the runes you lost.");
        add(CHAMPION, ROOT, "runeborn:sovereign_fragment", Frame.TASK, "Champion's Bane", "Slay a champion.");
        add(SUMMON, CHAMPION, "runeborn:sigil_varghast", Frame.TASK, "The Summoning", "Call a Sovereign with its sigil.");
        for (BossSpec boss : Bosses.all()) {
            if (boss.summonable()) {
                String name = boss.name().startsWith("The ") ? "the " + boss.name().substring(4) : boss.name();
                add(fell(boss), SUMMON, "runeborn:" + boss.relicId(), Frame.GOAL, "Fell " + name,
                        "Defeat " + name + (boss.title().isEmpty() ? "." : ", " + boss.title() + "."));
            }
        }
        add(CROWN, SUMMON, "runeborn:crown_shard_morgrath", Frame.CHALLENGE, "The Crown Remade",
                "Claim all " + CrownShard.values().length + " crown shards.");
    }

    public static List<Feat> all() {
        return Collections.unmodifiableList(new ArrayList<>(ALL.values()));
    }

    public static Feat get(String id) {
        return ALL.get(id);
    }
}
