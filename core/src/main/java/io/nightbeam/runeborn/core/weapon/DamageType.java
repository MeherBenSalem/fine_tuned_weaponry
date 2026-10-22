package io.nightbeam.runeborn.core.weapon;

import io.nightbeam.runeborn.core.stat.Attr;

import java.util.EnumSet;
import java.util.Set;

public enum DamageType {
    PHYSICAL(0xD9D2C2, EnumSet.of(Attr.STRENGTH, Attr.DEXTERITY, Attr.ARCANE)),
    MAGIC(0x6FA8E8, EnumSet.of(Attr.INTELLIGENCE)),
    FIRE(0xE8743C, EnumSet.of(Attr.STRENGTH, Attr.FAITH)),
    LIGHTNING(0xE8D35A, EnumSet.of(Attr.DEXTERITY)),
    HOLY(0xF2E3A8, EnumSet.of(Attr.FAITH));

    public static final DamageType[] ALL = values();

    public final int color;
    /** Attributes whose scaling coefficients add to this damage type. */
    public final Set<Attr> scaledBy;

    DamageType(int color, Set<Attr> scaledBy) {
        this.color = color;
        this.scaledBy = scaledBy;
    }

    public String langKey() {
        return "runeborn.damage." + name().toLowerCase();
    }
}
