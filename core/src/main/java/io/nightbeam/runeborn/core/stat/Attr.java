package io.nightbeam.runeborn.core.stat;

/** The eight character attributes. Order is part of the save and network format. */
public enum Attr {
    VIGOR("vig", 0xC0463C),
    MIND("mnd", 0x4F7BD8),
    ENDURANCE("end", 0x58A65C),
    STRENGTH("str", 0xC98B3C),
    DEXTERITY("dex", 0xD8C55A),
    INTELLIGENCE("int", 0x6FA8E8),
    FAITH("fth", 0xE8D08A),
    ARCANE("arc", 0xA46FD8);

    public static final Attr[] ALL = values();
    public static final int COUNT = ALL.length;
    public static final int MIN = 1;
    public static final int MAX = 99;

    public final String code;
    public final int color;

    Attr(String code, int color) {
        this.code = code;
        this.color = color;
    }

    public String langKey() {
        return "runeborn.attr." + name().toLowerCase();
    }

    public String shortKey() {
        return "runeborn.attr.short." + code;
    }

    public static Attr byCode(String code) {
        for (Attr a : ALL) {
            if (a.code.equalsIgnoreCase(code) || a.name().equalsIgnoreCase(code)) {
                return a;
            }
        }
        return null;
    }
}
