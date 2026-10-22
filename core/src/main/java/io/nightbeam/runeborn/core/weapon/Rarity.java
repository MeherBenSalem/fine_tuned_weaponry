package io.nightbeam.runeborn.core.weapon;

public enum Rarity {
    COMMON(0xD8D2C4, "§f"),
    RARE(0x6FA8E8, "§9"),
    EPIC(0xB07CE8, "§d"),
    LEGENDARY(0xE8C266, "§6");

    public final int color;
    public final String code;

    Rarity(int color, String code) {
        this.color = color;
        this.code = code;
    }

    public String langKey() {
        return "runeborn.rarity." + name().toLowerCase();
    }
}
