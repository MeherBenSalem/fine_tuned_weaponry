package io.nightbeam.runeborn.core.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and saves {@link PlayerData} as JSON files inside the world folder. Independent of
 * Minecraft's own save formats, which change between versions and loaders.
 */
public final class PlayerStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path dir;
    private final Map<UUID, PlayerData> loaded = new ConcurrentHashMap<>();

    public PlayerStore(Path worldRoot) {
        this.dir = worldRoot.resolve("runeborn").resolve("players");
    }

    public PlayerData get(UUID id) {
        return loaded.computeIfAbsent(id, this::read);
    }

    public boolean isLoaded(UUID id) {
        return loaded.containsKey(id);
    }

    public void unload(UUID id) {
        PlayerData data = loaded.remove(id);
        if (data != null) {
            write(id, data);
        }
    }

    public void saveAll() {
        loaded.forEach(this::write);
    }

    public void clear() {
        loaded.clear();
    }

    private PlayerData read(UUID id) {
        Path file = dir.resolve(id + ".json");
        if (Files.isRegularFile(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                PlayerData data = GSON.fromJson(reader, PlayerData.class);
                if (data != null) {
                    return data.sanitize();
                }
            } catch (IOException | RuntimeException e) {
                System.err.println("[runeborn] Could not read " + file + ": " + e);
                backup(file);
            }
        }
        return new PlayerData();
    }

    public void write(UUID id, PlayerData data) {
        try {
            Files.createDirectories(dir);
            Path file = dir.resolve(id + ".json");
            Path tmp = dir.resolve(id + ".json.tmp");
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[runeborn] Could not save player " + id + ": " + e);
        }
    }

    private static void backup(Path file) {
        try {
            Files.copy(file, file.resolveSibling(file.getFileName() + ".corrupt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            // Best effort; the original stays in place either way.
        }
    }
}
