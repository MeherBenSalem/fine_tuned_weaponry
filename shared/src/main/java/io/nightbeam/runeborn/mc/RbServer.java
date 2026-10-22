package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.Runeborn;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.config.ConfigIO;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.PlayerStore;
import io.nightbeam.runeborn.core.player.Session;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Server-wide state: the running server, player store, sessions and a small task scheduler. */
public final class RbServer {
    private static MinecraftServer server;
    private static PlayerStore store;
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final List<Task> TASKS = new ArrayList<>();
    private static final List<Task> PENDING = new ArrayList<>();
    private static long ticks;

    private record Task(long at, Runnable action) {
    }

    public static void start(MinecraftServer s) {
        server = s;
        ServerConfig.INSTANCE = ConfigIO.load(Platform.configDir().resolve("runeborn-server.json"), ServerConfig.class,
                ServerConfig::new).sanitize();
        store = new PlayerStore(s.getWorldPath(LevelResource.ROOT));
        ticks = 0;
        TASKS.clear();
        PENDING.clear();
        SESSIONS.clear();
        Runeborn.LOG.info("Runeborn ready: {} weapons, {} items", io.nightbeam.runeborn.core.weapon.Weapons.all().size(),
                RbItems.all().size());
    }

    public static void stop() {
        if (store != null) {
            store.saveAll();
            store.clear();
        }
        BossManager.clear();
        Status.clear();
        SESSIONS.clear();
        TASKS.clear();
        PENDING.clear();
        server = null;
        store = null;
    }

    public static boolean running() {
        return server != null;
    }

    public static MinecraftServer server() {
        return server;
    }

    public static long now() {
        return ticks;
    }

    public static PlayerData data(ServerPlayer player) {
        return store.get(player.getUUID());
    }

    public static Session session(ServerPlayer player) {
        return SESSIONS.computeIfAbsent(player.getUUID(), id -> new Session());
    }

    public static void save(ServerPlayer player) {
        if (store != null) {
            store.write(player.getUUID(), data(player));
        }
    }

    public static void unload(ServerPlayer player) {
        if (store != null) {
            store.unload(player.getUUID());
        }
        SESSIONS.remove(player.getUUID());
    }

    /** Runs {@code action} on the server thread after {@code delay} ticks. */
    public static void later(int delay, Runnable action) {
        PENDING.add(new Task(ticks + Math.max(1, delay), action));
    }

    public static void tick() {
        ticks++;
        if (!PENDING.isEmpty()) {
            TASKS.addAll(PENDING);
            PENDING.clear();
        }
        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.at <= ticks) {
                it.remove();
                try {
                    t.action.run();
                } catch (RuntimeException e) {
                    Runeborn.LOG.error("Runeborn scheduled task failed", e);
                }
            }
        }
        if (ticks % 6000 == 0 && store != null) {
            store.saveAll();
        }
    }

    private RbServer() {
    }
}
