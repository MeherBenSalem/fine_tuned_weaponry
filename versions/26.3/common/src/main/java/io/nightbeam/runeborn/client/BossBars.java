package io.nightbeam.runeborn.client;

import io.nightbeam.runeborn.mixin.BossOverlayAccessor;
import net.minecraft.client.Minecraft;

/** Counts vanilla boss bars on screen. */
final class BossBars {
    static int count() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) {
            return 0;
        }
        return ((BossOverlayAccessor) mc.gui.hud.getBossOverlay()).runeborn$events().size();
    }

    private BossBars() {
    }
}
