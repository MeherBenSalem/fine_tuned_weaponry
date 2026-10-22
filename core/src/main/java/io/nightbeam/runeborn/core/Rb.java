package io.nightbeam.runeborn.core;

/**
 * Global constants shared by every Minecraft version and loader.
 * Nothing in the {@code core} package may import Minecraft classes.
 */
public final class Rb {
    public static final String MOD_ID = "runeborn";
    public static final String NAME = "Runeborn";

    /** Health and damage are shown to players multiplied by this, so 2.5 damage reads as "25". */
    public static final int DISPLAY_SCALE = 10;

    public static final int TICKS_PER_SECOND = 20;

    public static String id(String path) {
        return MOD_ID + ":" + path;
    }

    private Rb() {
    }
}
