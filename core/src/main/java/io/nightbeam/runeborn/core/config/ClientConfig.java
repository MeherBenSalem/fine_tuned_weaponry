package io.nightbeam.runeborn.core.config;

/** Per-player HUD preferences, stored as {@code config/runeborn-client.json}. */
public final class ClientConfig {
    public static ClientConfig INSTANCE = new ClientConfig();

    public boolean hudEnabled = true;
    public boolean hideVanillaHearts = true;
    public boolean showBarNumbers = false;
    public boolean showBanners = true;
    public boolean showTargetBar = true;
    public boolean showBossBars = true;
    public boolean showEquipment = true;
    public int hudOffsetX = 0;
    public int hudOffsetY = 0;
    public float hudScale = 1.0f;
}
