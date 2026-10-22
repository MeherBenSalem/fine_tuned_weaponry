package io.nightbeam.runeborn.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.config.ConfigIO;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.feat.Feat;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.item.Recipes;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Buf;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.PlayerStore;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Leveling;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.AttackRating;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.Upgrades;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Dependency-free checks for the pure-Java core (catalog integrity, formulas, wire format,
 * persistence). Run from the repository root: exits non-zero on the first failing group.
 */
public final class CoreTests {
    private static final Pattern ID = Pattern.compile("[a-z0-9_]+");
    private static int checks;
    private static int failures;

    public static void main(String[] args) throws Exception {
        run("weapon catalog", CoreTests::weaponCatalog);
        run("materials and bosses", CoreTests::materialsAndBosses);
        run("origins", CoreTests::origins);
        run("feats and recipes", CoreTests::featsAndRecipes);
        run("leveling", CoreTests::leveling);
        run("attack rating", CoreTests::attackRating);
        run("upgrades", CoreTests::upgrades);
        run("buf", CoreTests::buf);
        run("packets", CoreTests::packets);
        run("player store", CoreTests::playerStore);
        run("config", CoreTests::config);
        run("generated lang", CoreTests::generatedLang);
        System.out.println((failures == 0 ? "CORE TESTS PASSED" : "CORE TESTS FAILED") + " (" + checks + " checks, "
                + failures + " failed groups)");
        if (failures > 0) {
            System.exit(1);
        }
    }

    private interface Group {
        void run() throws Exception;
    }

    private static void run(String name, Group group) {
        try {
            group.run();
            System.out.println("PASS " + name);
        } catch (Throwable t) {
            failures++;
            System.out.println("FAIL " + name + ": " + t);
        }
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    // ------------------------------------------------------------------ catalog

    private static void weaponCatalog() {
        List<WeaponSpec> all = Weapons.all();
        check(all.size() >= 70, "expected at least 70 weapons, got " + all.size());
        Set<String> ids = new HashSet<>();
        for (WeaponSpec w : all) {
            check(ID.matcher(w.id).matches(), "bad weapon id " + w.id);
            check(ids.add(w.id), "duplicate weapon id " + w.id);
            check(Weapons.get(w.id) == w, "lookup mismatch for " + w.id);
            check(w.name != null && !w.name.isBlank(), "unnamed weapon " + w.id);
            check(w.cls != null && w.rarity != null && w.path != null, "incomplete weapon " + w.id);
            check(w.art != null, "weapon without art " + w.id);
            check(w.model != null && (w.model.startsWith("sprite:") || w.model.startsWith("custom:")),
                    "bad model for " + w.id);
            check(w.baseAttack() > 0, "weapon without damage " + w.id);
            for (Attr a : Attr.ALL) {
                int req = w.requirement(a);
                check(req >= 0 && req <= Attr.MAX, "requirement out of range on " + w.id);
                check(w.scaling(a) >= 0, "negative scaling on " + w.id);
            }
            for (StatusType t : StatusType.ALL) {
                check(w.status(t) >= 0, "negative status on " + w.id);
            }
            if (w.cls.isCatalyst()) {
                check(!w.infusable(), "catalyst marked infusable " + w.id);
            }
        }
    }

    private static void materialsAndBosses() {
        Set<String> ids = new HashSet<>();
        for (MaterialSpec m : Materials.all()) {
            check(ID.matcher(m.id()).matches(), "bad material id " + m.id());
            check(ids.add(m.id()), "duplicate material id " + m.id());
            check(Weapons.get(m.id()) == null, "material id collides with weapon " + m.id());
            check(m.stack() >= 1 && m.stack() <= 64, "bad stack size on " + m.id());
        }
        check(Materials.get("ember_flask") != null && Materials.get("azure_flask") != null, "flasks missing");
        for (BossSpec b : Bosses.all()) {
            check(b.level() > 0 && b.runes() > 0, "bad boss rewards " + b.id());
            // Vanilla bosses keep their own stats (0 = unchanged); summoned sovereigns define theirs.
            check(!b.summonable() || (b.health() > 0 && b.damage() > 0), "bad boss stats " + b.id());
            check(!b.summonable() || Materials.get(b.sigilId()) != null, "summonable boss without sigil " + b.id());
            if (b.weapon() != null && !b.weapon().isEmpty()) {
                check(Weapons.get(b.weapon()) != null, "boss " + b.id() + " drops unknown weapon " + b.weapon());
            }
            for (String relic : b.relics()) {
                check(Weapons.get(relic) != null || Materials.get(relic) != null,
                        "boss " + b.id() + " has unknown relic " + relic);
            }
        }
    }

    private static void origins() {
        for (Origin o : Origin.ALL) {
            check(o.attrs().length == Attr.COUNT, "origin attr count " + o);
            check(o.level() == Leveling.level(o.attrs()), "origin level mismatch " + o);
            check(Weapons.get(o.weapon) != null, "origin " + o + " starts with unknown weapon " + o.weapon);
            check(Origin.byId(o.id()) == o, "origin lookup " + o);
            for (Origin.KitItem item : o.kit()) {
                check(Weapons.get(item.id()) != null || Materials.get(item.id()) != null,
                        "origin " + o + " kit has unknown item " + item.id());
            }
        }
    }

    /** Runeborn ids must exist; vanilla ids are trusted (the game rejects unknown ones on load). */
    private static boolean knownItem(String id) {
        if (id.startsWith("minecraft:")) {
            return true;
        }
        String path = id.substring(id.indexOf(':') + 1);
        return id.startsWith(Rb.MOD_ID + ":")
                && (Weapons.get(path) != null || Materials.get(path) != null || path.equals("ember_shrine"));
    }

    private static void featsAndRecipes() {
        Set<String> seen = new HashSet<>();
        int roots = 0;
        for (Feat f : Feat.all()) {
            check(knownItem(f.icon), "feat " + f.id + " has unknown icon " + f.icon);
            if (f.parent == null) {
                roots++;
            } else {
                check(seen.contains(f.parent), "feat " + f.id + " comes before its parent " + f.parent);
            }
            seen.add(f.id);
        }
        check(roots == 1, "exactly one root feat");
        for (String feat : Feat.LEVEL_FEATS) {
            check(Feat.get(feat) != null, "missing level feat " + feat);
        }
        check(Feat.LEVEL_FEATS.length == Feat.LEVEL_THRESHOLDS.length, "level feats and thresholds pair up");
        for (Recipes.Recipe r : Recipes.all()) {
            check(knownItem(r.result()), "recipe makes unknown item " + r.result());
            for (String cell : r.grid()) {
                check(cell == null || knownItem(cell), "recipe for " + r.result() + " uses unknown item " + cell);
            }
            if (r.shaped()) {
                check(r.pattern().size() <= 3 && r.pattern().stream().allMatch(row -> row.length() <= 3),
                        "pattern too large for " + r.result());
            } else {
                check(r.ingredients().size() <= 9, "too many ingredients for " + r.result());
            }
        }
        check(Recipes.forResult(Rb.id("ember_shrine")) != null, "the shrine must be craftable");
    }

    // ------------------------------------------------------------------ formulas

    private static void leveling() {
        int[] base = new int[Attr.COUNT];
        Arrays.fill(base, Attr.MIN);
        check(Leveling.level(base) == 1, "minimum level is 1");
        int[] max = new int[Attr.COUNT];
        Arrays.fill(max, Attr.MAX);
        check(Leveling.level(max) == Leveling.MAX_LEVEL, "max level");
        long previous = 0;
        for (int l = 1; l < Leveling.MAX_LEVEL; l++) {
            long cost = Leveling.cost(l);
            check(cost > 0 && cost >= previous, "level cost must grow at " + l);
            previous = cost;
        }
        check(Leveling.costRange(10, 3, 1) == Leveling.cost(10) + Leveling.cost(11) + Leveling.cost(12), "cost range");
        check(Leveling.cost(50, 0.5) < Leveling.cost(50), "cost multiplier");
    }

    private static void attackRating() {
        int[] maxed = new int[Attr.COUNT];
        Arrays.fill(maxed, Attr.MAX);
        int[] weak = new int[Attr.COUNT];
        Arrays.fill(weak, Attr.MIN);
        for (WeaponSpec w : Weapons.all()) {
            AttackRating base = AttackRating.of(w, 0, Affinity.STANDARD, maxed);
            AttackRating top = AttackRating.of(w, w.path.max, Affinity.STANDARD, maxed);
            check(base.total > 0, "zero attack rating for " + w.id);
            check(!base.anyUnmet, "requirements unmet at 99 for " + w.id);
            check(top.total > base.total, "upgrades must raise attack rating for " + w.id);
            boolean hasReq = false;
            for (Attr a : Attr.ALL) {
                hasReq |= w.requirement(a) > Attr.MIN;
            }
            if (hasReq) {
                AttackRating unmet = AttackRating.of(w, 0, Affinity.STANDARD, weak);
                check(unmet.anyUnmet && unmet.total < base.total, "unmet requirements must hurt " + w.id);
            }
            if (w.infusable()) {
                for (Affinity a : Affinity.ALL) {
                    check(AttackRating.of(w, 0, a, maxed).total > 0, "affinity " + a + " zeroes " + w.id);
                }
            }
        }
    }

    private static void upgrades() {
        for (WeaponSpec w : Weapons.all()) {
            for (int level = 0; level < w.path.max; level++) {
                Upgrades.Cost cost = Upgrades.next(w, level, 1);
                check(cost != null, "missing upgrade cost for " + w.id + " +" + level);
                check(Materials.get(cost.materialId()) != null, "unknown upgrade material " + cost.materialId());
                check(cost.count() > 0 && cost.runes() > 0, "free upgrade for " + w.id);
            }
            check(Upgrades.next(w, w.path.max, 1) == null, "upgrade past max for " + w.id);
        }
        check(Upgrades.infusionCost(1) > 0, "infusion cost");
    }

    // ------------------------------------------------------------------ wire format

    private static void buf() {
        int[] ints = {0, 1, -1, 127, 128, 300, Integer.MAX_VALUE, Integer.MIN_VALUE};
        long[] longs = {0, 1, -1, 1L << 40, Long.MAX_VALUE, Long.MIN_VALUE};
        Buf w = Buf.writer();
        for (int i : ints) {
            w.varInt(i);
        }
        for (long l : longs) {
            w.varLong(l);
        }
        w.str("Ember Shrine · ✦ Ünïcode").bool(true).bool(false).f32(1.5f).f64(-2.25).ints(new int[]{4, 5, 6});
        Buf r = Buf.reader(w.bytes());
        for (int i : ints) {
            check(r.readVarInt() == i, "varInt " + i);
        }
        for (long l : longs) {
            check(r.readVarLong() == l, "varLong " + l);
        }
        check(r.readStr().equals("Ember Shrine · ✦ Ünïcode"), "string");
        check(r.readBool() && !r.readBool(), "bools");
        check(r.readF32() == 1.5f && r.readF64() == -2.25, "floats");
        check(Arrays.equals(r.readInts(8), new int[]{4, 5, 6}), "ints");
        check(r.remaining() == 0, "trailing bytes");
        boolean threw;
        try {
            Buf.reader(new byte[0]).readVarInt();
            threw = false;
        } catch (RuntimeException e) {
            threw = true;
        }
        check(threw, "reading past the end must throw");
    }

    private static void packets() {
        PlayerData d = new PlayerData();
        d.origin = Origin.ALL[0].id();
        d.attrs = Origin.ALL[0].attrs();
        d.runes = 123_456;
        d.runesEarned = 999_999;
        d.flaskCharges = 6;
        d.flaskEmberShare = 4;
        d.emberLeft = 2;
        d.azureLeft = 1;
        d.lost = new PlayerData.LostRunes(500, "minecraft:overworld", 1.5, 64, -3.25);
        d.shrines.add(new ShrineRecord("Ashen Hollow", "minecraft:overworld", 10, 70, -20));
        d.rested = d.shrines.get(0).copy();
        d.shards.add("ash");
        d.equippedShard = "ash";
        d.felled.add("kaelthar");
        d.kills = 42;
        Buf in = Buf.reader(Packets.sync(d, new ServerConfig()));
        check(in.readVarInt() == Packets.SYNC, "sync id");
        PlayerData back = Packets.readSync(in).data;
        check(back.origin.equals(d.origin) && Arrays.equals(back.attrs, d.attrs), "sync attrs");
        check(back.runes == d.runes && back.runesEarned == d.runesEarned, "sync runes");
        check(back.lost != null && back.lost.amount == 500 && back.lost.z == -3.25, "sync lost runes");
        check(back.shrines.size() == 1 && back.shrines.get(0).at("minecraft:overworld", 10, 70, -20), "sync shrines");
        check(back.rested != null && back.equippedShard.equals("ash") && back.felled.contains("kaelthar"), "sync relics");
        check(back.kills == 42 && back.emberLeft == 2, "sync counters");

        Buf t = Buf.reader(Packets.target(new Packets.Target(7, "Grave Knight", 12, 30, 40, 5, true, 0xFFAA00,
                new float[StatusType.ALL.length])));
        check(t.readVarInt() == Packets.TARGET, "target id");
        Packets.Target target = Packets.readTarget(t);
        check(target.entityId() == 7 && target.name().equals("Grave Knight") && target.elite(), "target fields");

        Buf bars = Buf.reader(Packets.bosses(List.of(new Packets.BossBar(3, "Kaelthar", 100, 200, 0xC0463C, 2))));
        check(bars.readVarInt() == Packets.BOSSES, "bosses id");
        List<Packets.BossBar> list = Packets.readBosses(bars);
        check(list.size() == 1 && list.get(0).phase() == 2 && list.get(0).maxHealth() == 200, "boss bar fields");

        Buf open = Buf.reader(Packets.open(Packets.Screen.values()[0], "Ashen Hollow", "minecraft:overworld", 1, 2, 3));
        check(open.readVarInt() == Packets.OPEN, "open id");
        Packets.Open o = Packets.readOpen(open);
        check(o.shrineName().equals("Ashen Hollow") && o.z() == 3, "open fields");
    }

    // ------------------------------------------------------------------ persistence

    private static void playerStore() throws Exception {
        Path world = Files.createTempDirectory("runeborn-test");
        UUID id = UUID.randomUUID();
        PlayerStore store = new PlayerStore(world);
        PlayerData d = store.get(id);
        d.runes = 77;
        d.attrs[Attr.VIGOR.ordinal()] = 40;
        store.saveAll();
        store.clear();
        PlayerData back = store.get(id);
        check(back.runes == 77 && back.attr(Attr.VIGOR) == 40, "player data round trip");

        UUID broken = UUID.randomUUID();
        Path file = world.resolve("runeborn").resolve("players").resolve(broken + ".json");
        Files.writeString(file, "{ not json", StandardCharsets.UTF_8);
        PlayerData fresh = store.get(broken);
        check(fresh.runes == 0 && !fresh.hasOrigin(), "corrupt file falls back to fresh data");

        Path tampered = world.resolve("runeborn").resolve("players").resolve(UUID.randomUUID() + ".json");
        Files.writeString(tampered, "{\"attrs\":[500,0,0],\"runes\":-5,\"flaskCharges\":99}", StandardCharsets.UTF_8);
        store.clear();
        PlayerData clamped = store.get(UUID.fromString(tampered.getFileName().toString().replace(".json", "")));
        check(clamped.attrs.length == Attr.COUNT && clamped.runes == 0
                && clamped.flaskCharges == PlayerData.MAX_FLASK_CHARGES, "sanitize clamps tampered data");
    }

    private static void config() throws Exception {
        Path file = Files.createTempDirectory("runeborn-cfg").resolve("runeborn-server.json");
        ServerConfig defaults = ConfigIO.load(file, ServerConfig.class, ServerConfig::new);
        check(Files.isRegularFile(file), "default config written");
        defaults.levelCostMultiplier = 2.5;
        ConfigIO.save(file, defaults);
        ServerConfig back = ConfigIO.load(file, ServerConfig.class, ServerConfig::new);
        check(back.levelCostMultiplier == 2.5, "config round trip");
    }

    /** The committed en_us.json must match core; re-run tools/assetgen/generate.py if this fails. */
    private static void generatedLang() throws Exception {
        Path lang = Path.of("shared/src/main/resources/assets/runeborn/lang/en_us.json");
        check(Files.isRegularFile(lang), "run from the repository root");
        JsonObject json = JsonParser.parseString(Files.readString(lang, StandardCharsets.UTF_8)).getAsJsonObject();
        Lang.english().forEach((key, english) -> check(json.has(key) && json.get(key).getAsString().equals(english),
                "en_us.json out of date for " + key));
        for (WeaponSpec w : Weapons.all()) {
            check(json.has(w.langKey()), "missing weapon name " + w.langKey());
        }
        for (MaterialSpec m : Materials.all()) {
            check(json.has(m.langKey()), "missing material name " + m.langKey());
        }
        for (BossSpec b : Bosses.all()) {
            check(json.has(b.nameKey()), "missing boss name " + b.nameKey());
        }
    }

    private CoreTests() {
    }
}
