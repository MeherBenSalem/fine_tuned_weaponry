package io.nightbeam.runeborn.core.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.item.Recipes;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dumps the catalog for the asset generator ({@code tools/assetgen}). Not used at runtime.
 * Usage: {@code java io.nightbeam.runeborn.core.tools.Export <out.json>}
 */
public final class Export {
    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonObject root = new JsonObject();

        JsonArray weapons = new JsonArray();
        for (WeaponSpec w : Weapons.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", w.id);
            o.addProperty("name", w.name);
            o.addProperty("class", w.cls.name().toLowerCase());
            o.addProperty("rarity", w.rarity.name().toLowerCase());
            o.addProperty("model", w.model);
            o.addProperty("catalyst", w.cls.isCatalyst());
            weapons.add(o);
        }
        root.add("weapons", weapons);

        JsonArray materials = new JsonArray();
        for (MaterialSpec m : Materials.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", m.id());
            o.addProperty("name", m.name());
            o.addProperty("kind", m.kind().name().toLowerCase());
            o.addProperty("rarity", m.rarity().name().toLowerCase());
            o.addProperty("texture", m.texture());
            o.addProperty("value", m.value());
            o.addProperty("ref", m.ref());
            materials.add(o);
        }
        root.add("materials", materials);

        JsonArray bosses = new JsonArray();
        for (BossSpec b : Bosses.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", b.id());
            o.addProperty("name", b.name());
            o.addProperty("summonable", b.summonable());
            o.addProperty("color", String.format("%06X", b.color()));
            o.addProperty("sigil_key", b.sigilRecipe());
            o.addProperty("has_shard", b.shard() != null);
            bosses.add(o);
        }
        root.add("bosses", bosses);

        JsonArray recipes = new JsonArray();
        for (Recipes.Recipe r : Recipes.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("id", r.id());
            o.addProperty("result", r.result());
            o.addProperty("count", r.count());
            if (r.shaped()) {
                o.add("pattern", gson.toJsonTree(r.pattern()));
                JsonObject key = new JsonObject();
                r.key().forEach((ch, item) -> key.addProperty(String.valueOf(ch), item));
                o.add("key", key);
            } else {
                o.add("ingredients", gson.toJsonTree(r.ingredients()));
            }
            recipes.add(o);
        }
        root.add("recipes", recipes);

        JsonObject affinities = new JsonObject();
        for (Affinity a : Affinity.ALL) {
            affinities.addProperty(a.id(), String.format("%06X", a.color));
        }
        root.add("affinities", affinities);

        Map<String, String> lang = new TreeMap<>(Lang.english());
        root.add("lang", gson.toJsonTree(lang));

        Path out = Path.of(args.length > 0 ? args[0] : "catalog.json");
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        Files.writeString(out, gson.toJson(root), StandardCharsets.UTF_8);
        System.out.println("Exported " + weapons.size() + " weapons, " + materials.size() + " materials, " + lang.size()
                + " lang keys to " + out);
    }

    private Export() {
    }
}
