package io.nightbeam.runeborn.core.mob;

import java.util.Random;

/** Champion modifiers. A champion carries one affix, two above level 30. */
public enum EliteAffix {
    IRONCLAD("Ironclad", 0xA8A8B8),
    FRENZIED("Frenzied", 0xE8743C),
    STORMTOUCHED("Stormtouched", 0xE8D35A),
    BLOODTHIRSTY("Bloodthirsty", 0xB3262B),
    FROSTBORNE("Frostborne", 0x9ED8F0),
    BLAZING("Blazing", 0xF29D5A),
    VENOMOUS("Venomous", 0x7BAA3A),
    WARDED("Warded", 0xE8D08A),
    COLOSSAL("Colossal", 0xC98B3C),
    PHASING("Phasing", 0xA46FD8);

    public static final EliteAffix[] ALL = values();

    public final String title;
    public final int color;

    EliteAffix(String title, int color) {
        this.title = title;
        this.color = color;
    }

    public String tag() {
        return "runeborn.affix." + name().toLowerCase();
    }

    public String langKey() {
        return "runeborn.affix." + name().toLowerCase();
    }

    public static EliteAffix random(Random random) {
        return ALL[random.nextInt(ALL.length)];
    }

    public static EliteAffix fromTag(String tag) {
        for (EliteAffix a : ALL) {
            if (a.tag().equals(tag)) {
                return a;
            }
        }
        return null;
    }
}
