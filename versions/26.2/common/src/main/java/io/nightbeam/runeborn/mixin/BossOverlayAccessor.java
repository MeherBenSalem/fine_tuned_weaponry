package io.nightbeam.runeborn.mixin;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

/** Lets the HUD place the target bar below vanilla boss bars. */
@Mixin(BossHealthOverlay.class)
public interface BossOverlayAccessor {
    @Accessor("events")
    Map<UUID, LerpingBossEvent> runeborn$events();
}
