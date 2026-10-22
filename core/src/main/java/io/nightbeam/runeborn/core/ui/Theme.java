package io.nightbeam.runeborn.core.ui;

/** Palette and spacing for every Runeborn screen: dark glass, thin bronze rules, pale gold type. */
public final class Theme {
    public static final int SCRIM = 0xB0060504;
    public static final int PANEL = 0xE80D0C0A;
    public static final int PANEL_TOP = 0xF0171512;
    public static final int PANEL_BOTTOM = 0xF0090807;
    public static final int PANEL_SOFT = 0x9C0D0C0A;
    public static final int BORDER = 0xFF5C4C30;
    public static final int BORDER_DIM = 0x803E3322;
    public static final int HIGHLIGHT = 0x26E8D8B0;
    public static final int ROW_HOVER = 0x40D9B97A;
    public static final int ROW_SELECTED = 0x70D9B97A;

    public static final int GOLD = 0xFFE3C98A;
    public static final int GOLD_BRIGHT = 0xFFF5E2A8;
    public static final int GOLD_DIM = 0xFF9C8558;
    public static final int TEXT = 0xFFEDE7DA;
    public static final int TEXT_DIM = 0xFF9A9384;
    public static final int TEXT_FAINT = 0xFF6B655B;
    public static final int GOOD = 0xFF8FD18A;
    public static final int BAD = 0xFFE06A5A;
    public static final int BLUE = 0xFF7FAEE8;

    public static final int HP = 0xFFA3241F;
    public static final int HP_LIGHT = 0xFFD0453A;
    public static final int FP = 0xFF2C55B8;
    public static final int FP_LIGHT = 0xFF4F7FE0;
    public static final int STAMINA = 0xFF3D8A3B;
    public static final int STAMINA_LIGHT = 0xFF62B45C;
    public static final int BAR_BACK = 0xC0100E0C;
    public static final int BAR_CHIP = 0xFFE8C870;

    public static final int PAD = 8;
    public static final int ROW = 14;

    public static int alpha(int argb, float a) {
        int base = (argb >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, Math.round(base * a)));
        return (na << 24) | (argb & 0xFFFFFF);
    }

    public static int opaque(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static int mix(int a, int b, float t) {
        t = Math.max(0, Math.min(1, t));
        int aa = (a >>> 24) & 0xFF, ar = (a >>> 16) & 0xFF, ag = (a >>> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >>> 16) & 0xFF, bg = (b >>> 8) & 0xFF, bb = b & 0xFF;
        return (Math.round(aa + (ba - aa) * t) << 24) | (Math.round(ar + (br - ar) * t) << 16)
                | (Math.round(ag + (bg - ag) * t) << 8) | Math.round(ab + (bb - ab) * t);
    }

    private Theme() {
    }
}
