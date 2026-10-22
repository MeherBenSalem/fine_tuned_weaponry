package io.nightbeam.runeborn.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;
import java.util.ServiceLoader;

/** Loader services (Fabric or Forge), found with {@link ServiceLoader}. */
public final class Platform {
    public interface Service {
        String loader();

        Path configDir();

        boolean isClient();

        void send(ServerPlayer player, byte[] payload);

        void sendToServer(byte[] payload);

        CreativeModeTab.Builder tabBuilder();

        /** The loader's entity reach attribute, or null when the loader has none (Fabric on 1.20.1). */
        Attribute entityReach();
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

    public static Attribute entityReach() {
        return IMPL.entityReach();
    }

    private Platform() {
    }
}
