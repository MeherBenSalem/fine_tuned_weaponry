package io.nightbeam.runeborn.core.player;

import java.util.HashMap;
import java.util.Map;

/** Per-player state that lives only while the player is online. Times are server ticks. */
public final class Session {
    public double stamina = -1;
    public long staminaRegenAt;
    public long invulnerableUntil;
    public long holyBuffUntil;
    public long warCryUntil;
    public long lastFlaskAt;
    public long lastDodgeAt;
    public long lastTravelAt;
    public final Map<String, Long> cooldowns = new HashMap<>();

    /** Shrine the player opened most recently; actions are only accepted near it. */
    public String shrineDimension;
    public int shrineX;
    public int shrineY;
    public int shrineZ;

    // Sync bookkeeping
    public int lastTargetId = -1;
    public long lastTargetAt;
    public double lastHitDamage;
    public double sentFp = Double.NaN;
    public double sentStamina = Double.NaN;
    public long sentRunes = -1;
    public boolean dirty = true;

    public boolean onCooldown(String key, long now) {
        Long until = cooldowns.get(key);
        return until != null && until > now;
    }

    public long cooldownLeft(String key, long now) {
        Long until = cooldowns.get(key);
        return until == null ? 0 : Math.max(0, until - now);
    }

    public void cooldown(String key, long now, int ticks) {
        cooldowns.put(key, now + ticks);
    }

    public double stamina(double max) {
        return stamina < 0 ? max : Math.min(stamina, max);
    }

    public boolean hasShrine() {
        return shrineDimension != null;
    }

    public void clearShrine() {
        shrineDimension = null;
    }
}
