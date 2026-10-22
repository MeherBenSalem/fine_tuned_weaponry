package io.nightbeam.runeborn.core.weapon;

/** Letter grade for a scaling coefficient, as shown in tooltips. */
public enum Grade {
    NONE("-", 0x6B655B),
    E("E", 0x9A9384),
    D("D", 0xB8B0A0),
    C("C", 0xD8D2C4),
    B("B", 0x8FC7F0),
    A("A", 0xE8C266),
    S("S", 0xF29D5A);

    public final String letter;
    public final int color;

    Grade(String letter, int color) {
        this.letter = letter;
        this.color = color;
    }

    public static Grade of(double coefficient) {
        double c = coefficient * 100;
        if (c <= 0.5) {
            return NONE;
        }
        if (c < 25) {
            return E;
        }
        if (c < 60) {
            return D;
        }
        if (c < 90) {
            return C;
        }
        if (c < 140) {
            return B;
        }
        if (c < 175) {
            return A;
        }
        return S;
    }
}
