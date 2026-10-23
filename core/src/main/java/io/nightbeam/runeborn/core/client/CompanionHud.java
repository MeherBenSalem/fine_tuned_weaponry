package io.nightbeam.runeborn.core.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Top-left screen space used by RPG Attribute System's level HUD, so Runeborn's bars can sit below
 * it. Read from that mod's own display settings ({@code config/ras/display/}); its HUD is 162x26
 * GUI pixels at scale 1 with a 6 pixel margin.
 */
public final class CompanionHud {
    private static final int RAS_WIDTH = 162;
    private static final int RAS_HEIGHT = 26;
    private static final int RAS_MARGIN = 6;

    private static Path rasConfig;
    private static boolean measured;
    private static int right;
    private static int bottom;

    /** Called once by the client when RPG Attribute System is installed. */
    public static void rpgAttributeSystem(Path configDir) {
        rasConfig = configDir.resolve("ras").resolve("display");
        measured = false;
    }

    /** Right edge of the reserved top-left area, 0 when nothing is reserved. */
    public static int right() {
        measure();
        return right;
    }

    /** Bottom edge of the reserved top-left area, 0 when nothing is reserved. */
    public static int bottom() {
        measure();
        return bottom;
    }

    /** Reads the settings lazily: the other mod writes them during its own start-up. */
    private static void measure() {
        if (measured || rasConfig == null) {
            return;
        }
        measured = true;
        JsonObject overlay = read(rasConfig.resolve("overlay.json"));
        JsonObject ui = read(rasConfig.resolve("ui.json"));
        String anchor = string(overlay, "anchor", "TL");
        if (anchor.contains("R") || anchor.contains("B")) {
            return;
        }
        double scale = Math.max(0.5, Math.min(2, number(ui, "hud_scale", 1)));
        int x = RAS_MARGIN + offset(overlay, "hudXOffset", "x_offset");
        int y = RAS_MARGIN + offset(overlay, "hudYOffset", "y_offset");
        right = x + (int) Math.round(RAS_WIDTH * scale) + 4;
        bottom = y + (int) Math.round(RAS_HEIGHT * scale) + 4;
    }

    /** Same rule as the other mod: the new key, or the legacy key when the new one is 0. */
    private static int offset(JsonObject o, String key, String legacyKey) {
        double value = number(o, key, 0);
        return (int) (value == 0 ? number(o, legacyKey, 0) : value);
    }

    private static JsonObject read(Path file) {
        if (!Files.isRegularFile(file)) {
            return new JsonObject();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        } catch (IOException | RuntimeException e) {
            return new JsonObject();
        }
    }

    private static String string(JsonObject o, String key, String fallback) {
        try {
            return o.has(key) ? o.get(key).getAsString() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static double number(JsonObject o, String key, double fallback) {
        try {
            return o.has(key) ? o.get(key).getAsDouble() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private CompanionHud() {
    }
}
