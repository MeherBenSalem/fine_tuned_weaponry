package io.nightbeam.runeborn.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;
import java.util.ServiceLoader;

/** Loader services (Fabric or NeoForge), found with {@link ServiceLoader}. */
public final class Platform {
    public interface Service {
        String loader();

        Path configDir();

        boolean isClient();

        void send(ServerPlayer player, byte[] payload);

        void sendToServer(byte[] payload);

        CreativeModeTab.Builder tabBuilder();
    }

    private static final Service IMPL = ServiceLoader.load(Service.class, Platform.class.getClassLoader()).findFirst()
            .orElseThrow(() -> new IllegalStateException("No Runeborn platform service found"));

    public static String loader() {
        return IMPL.loader();
    }

    public static Path configDir() {
        return IMPL.configDir();
    }

    public static boolean isClient() {
        return IMPL.isClient();
    }

    public static void send(ServerPlayer player, byte[] payload) {
        IMPL.send(player, payload);
    }

    public static void sendToServer(byte[] payload) {
        IMPL.sendToServer(payload);
    }

    public static CreativeModeTab.Builder tabBuilder() {
        return IMPL.tabBuilder();
    }

    private Platform() {
    }
}
