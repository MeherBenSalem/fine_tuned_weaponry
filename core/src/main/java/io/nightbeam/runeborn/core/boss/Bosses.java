package io.nightbeam.runeborn.core.boss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.nightbeam.runeborn.core.boss.Ability.*;

/** Boss catalog: six summonable Sovereigns, a night-stalking field boss, and the vanilla bosses. */
public final class Bosses {
    private static final Map<String, BossSpec> BY_ID = new LinkedHashMap<>();
    private static final Map<String, BossSpec> BY_ENTITY = new LinkedHashMap<>();
    private static final List<BossSpec> ALL;

    static {
        add(new BossSpec("varghast", "Varghast the Grafted", "Sovereign of the Hollow Vale", "minecraft:vindicator",
                true, "graft_axe", 300, 10, 8, 0.36, 20, 18_000,
                List.of(GROUND_SLAM, SUMMON_HOLLOWS, CHARGE, ENRAGE), List.of("graft_axe", "crownsplitter"),
                CrownShard.GRAFTED, 0xC98B3C, "minecraft:iron_block"));
        add(new BossSpec("sylvara", "Sylvara, Frost Matron", "Sovereign of the Rime Veil", "minecraft:stray",
                true, "frostbound_staff", 280, 9, 6, 0.30, 25, 22_000,
                List.of(FROST_NOVA, ICE_VOLLEY, BLINK, ENRAGE), List.of("rimeveil", "frostbound_staff"),
                CrownShard.FROST, 0x9ED8F0, "minecraft:blue_ice"));
        add(new BossSpec("kaelthar", "Kaelthar the Stormcaller", "Sovereign of the Drowned Sky", "minecraft:evoker",
                true, "storm_totem", 320, 8, 6, 0.40, 30, 28_000,
                List.of(LIGHTNING_BARRAGE, BLINK, STORM_DASH, ENRAGE), List.of("stormcaller_pike", "storm_totem"),
                CrownShard.STORM, 0xE8D35A, "minecraft:lightning_rod"));
        add(new BossSpec("ysolde", "Ysolde, the Crimson Bloom", "Sovereign of the Scarlet Garden",
                "minecraft:wither_skeleton", true, "crimsonfall", 360, 11, 8, 0.34, 35, 36_000,
                List.of(BLOOD_FLURRY, BLINK, BLOOD_BLOOM, ENRAGE), List.of("crimsonfall", "bloodbloom_sigil"),
                CrownShard.BLOOM, 0xB3262B, "minecraft:redstone_block"));
        add(new BossSpec("morgrath", "Morgrath, Ash Sovereign", "Sovereign of the Burning Crown",
                "minecraft:piglin_brute", true, "voidstar_hammer", 520, 14, 12, 0.36, 50, 70_000,
                List.of(GROUND_SLAM, FIRE_PILLARS, METEOR_RAIN, ASH_CLOUD, ENRAGE),
                List.of("voidstar_hammer", "ashen_greatsword"), CrownShard.ASH, 0xE8743C, "minecraft:netherite_scrap"));
        add(new BossSpec("vaelorn", "Vaelorn, the Wyrm-Ravager", "Sovereign of the Last Shore", "minecraft:ravager",
                true, "", 700, 18, 14, 0.32, 70, 140_000,
                List.of(ROAR, CHARGE, LIGHTNING_BARRAGE, FIRE_PILLARS, ENRAGE),
                List.of("wyrmfang_halberd", "dragonsoul_seal"), CrownShard.WYRM, 0x8FC7A0, "minecraft:dragon_breath"));
        add(new BossSpec("dusk_rider", "The Dusk Rider", "Who Rides When Light Fails",
                "minecraft:skeleton+minecraft:skeleton_horse", true, "duskrider_glaive", 240, 10, 10, 0.30, 15, 12_000,
                List.of(CHARGE, NIGHTFALL, ENRAGE), List.of("duskrider_glaive", "sunlit_sword"), null, 0x6B6FA8,
                "minecraft:clock"));

        add(new BossSpec("ender_dragon", "The Ender Dragon", "", "minecraft:ender_dragon", false, "", 0, 0, 0, 0, 80,
                120_000, List.of(), List.of("lunar_bloomfang", "abyssal_scepter"), CrownShard.END, 0xB07CE8, ""));
        add(new BossSpec("wither", "The Wither", "", "minecraft:wither", false, "", 0, 0, 0, 0, 60, 80_000, List.of(),
                List.of("gravewarden_scythe", "starfall_slab"), null, 0x6B655B, ""));
        add(new BossSpec("warden", "The Warden", "", "minecraft:warden", false, "", 0, 0, 0, 0, 55, 60_000, List.of(),
                List.of("shadowfang", "eclipse_twinblade"), null, 0x2F6F73, ""));
        add(new BossSpec("elder_guardian", "The Elder Guardian", "", "minecraft:elder_guardian", false, "", 0, 0, 0, 0, 35,
                25_000, List.of(), List.of("stormfang", "bloomwarden_mace"), null, 0x6FA8A0, ""));
        ALL = Collections.unmodifiableList(new ArrayList<>(BY_ID.values()));
    }

    private static void add(BossSpec spec) {
        BY_ID.put(spec.id(), spec);
        if (!spec.summonable()) {
            BY_ENTITY.put(spec.entity(), spec);
        }
    }

    public static List<BossSpec> all() {
        return ALL;
    }

    public static BossSpec get(String id) {
        return BY_ID.get(id);
    }

    /** Vanilla boss mapping, by entity type id (e.g. {@code minecraft:wither}). */
    public static BossSpec vanilla(String entityTypeId) {
        return BY_ENTITY.get(entityTypeId);
    }

    public static BossSpec byShard(CrownShard shard) {
        for (BossSpec spec : ALL) {
            if (spec.shard() == shard) {
                return spec;
            }
        }
        return null;
    }

    private Bosses() {
    }
}
