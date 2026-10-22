package io.nightbeam.runeborn.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/** Loads a JSON config, writing defaults back so new keys appear after updates. */
public final class ConfigIO {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static <T> T load(Path file, Class<T> type, Supplier<T> defaults) {
        T value = null;
        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                value = GSON.fromJson(reader, type);
            } catch (IOException | RuntimeException e) {
                System.err.println("[runeborn] Could not read " + file + ", using defaults: " + e);
            }
        }
        if (value == null) {
            value = defaults.get();
        }
        save(file, value);
        return value;
    }

    public static void save(Path file, Object value) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(value, writer);
            }
        } catch (IOException e) {
            System.err.println("[runeborn] Could not write " + file + ": " + e);
        }
    }

    public static Gson gson() {
        return GSON;
    }

    private ConfigIO() {
    }
}
