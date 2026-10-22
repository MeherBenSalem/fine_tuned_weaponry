package io.nightbeam.runeborn.core.ui;

import java.util.ArrayList;
import java.util.List;

/** Immediate-mode drawing helpers shared by every screen and the HUD. */
public final class Ui {
    public static boolean hit(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    /** Dark glass panel with a bronze frame and a faint top highlight. */
    public static void panel(Canvas c, int x, int y, int w, int h) {
        c.gradient(x, y, x + w, y + h, Theme.PANEL_TOP, Theme.PANEL_BOTTOM);
        frame(c, x, y, w, h, Theme.BORDER);
        c.fill(x + 1, y + 1, x + w - 1, y + 2, Theme.HIGHLIGHT);
    }

    public static void softPanel(Canvas c, int x, int y, int w, int h) {
        c.fill(x, y, x + w, y + h, Theme.PANEL_SOFT);
        frame(c, x, y, w, h, Theme.BORDER_DIM);
    }

    public static void frame(Canvas c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + 1, color);
        c.fill(x, y + h - 1, x + w, y + h, color);
        c.fill(x, y + 1, x + 1, y + h - 1, color);
        c.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    /** Horizontal gradient approximated with vertical strips. */
    public static void hgradient(Canvas c, int x0, int y0, int x1, int y1, int left, int right) {
        int w = x1 - x0;
        if (w <= 0) {
            return;
        }
        int step = Math.max(1, w / 48);
        for (int x = x0; x < x1; x += step) {
            int end = Math.min(x1, x + step);
            float t = (x - x0 + step * 0.5f) / w;
            c.fill(x, y0, end, y1, Theme.mix(left, right, t));
        }
    }

    /** A separator that glows in the middle and fades at both ends. */
    public static void rule(Canvas c, int x, int y, int w, int color) {
        int clear = color & 0x00FFFFFF;
        int half = w / 2;
        hgradient(c, x, y, x + half, y + 1, clear, color);
        hgradient(c, x + half, y, x + w, y + 1, color, clear);
    }

    /** Selection band: bright at the left edge, fading right. */
    public static void band(Canvas c, int x, int y, int w, int h, int color) {
        hgradient(c, x, y, x + w, y + h, color, color & 0x00FFFFFF);
        c.fill(x, y, x + 1, y + h, Theme.opaque(color));
    }

    public static void button(Canvas c, int x, int y, int w, int h, String label, boolean hover, boolean enabled) {
        int top = enabled ? (hover ? 0xF0352C1E : 0xF0221D15) : 0xC0161412;
        int bottom = enabled ? (hover ? 0xF0201A12 : 0xF0141110) : 0xC0100E0C;
        c.gradient(x, y, x + w, y + h, top, bottom);
        frame(c, x, y, w, h, enabled ? (hover ? Theme.GOLD : Theme.BORDER) : Theme.BORDER_DIM);
        int color = enabled ? (hover ? Theme.GOLD_BRIGHT : Theme.GOLD) : Theme.TEXT_FAINT;
        centered(c, label, x + w / 2, y + (h - 8) / 2, color, false);
    }

    /** Small square +/- style button. */
    public static void stepper(Canvas c, int x, int y, String glyph, boolean hover, boolean enabled) {
        button(c, x, y, 11, 11, glyph, hover, enabled);
    }

    public static void centered(Canvas c, String s, int cx, int y, int color, boolean shadow) {
        c.text(s, cx - c.textWidth(s) / 2, y, color, shadow);
    }

    public static void right(Canvas c, String s, int rx, int y, int color, boolean shadow) {
        c.text(s, rx - c.textWidth(s), y, color, shadow);
    }

    public static void titleCentered(Canvas c, String s, int cx, float y, int color, float scale) {
        c.title(s, cx - c.titleWidth(s) * scale / 2f, y, color, scale);
    }

    /** Status bar with background, fill, optional trailing "chip" segment and a thin frame. */
    public static void bar(Canvas c, int x, int y, int w, int h, float value, float chip, int fill, int light) {
        c.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2A241A);
        c.fill(x, y, x + w, y + h, Theme.BAR_BACK);
        int fw = Math.round(w * clamp01(value));
        int cw = Math.round(w * clamp01(chip));
        if (cw > fw) {
            c.fill(x + fw, y, x + cw, y + h, Theme.BAR_CHIP);
        }
        if (fw > 0) {
            c.gradient(x, y, x + fw, y + h, light, fill);
            c.fill(x, y, x + fw, y + 1, Theme.alpha(0xFFFFFFFF, 0.18f));
        }
    }

    /** Filled diamond, used as the rune glyph and list bullets. */
    public static void diamond(Canvas c, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int half = r - Math.abs(dy);
            c.fill(cx - half, cy + dy, cx + half + 1, cy + dy + 1, color);
        }
    }

    /** The rune currency glyph: a golden diamond with a dark core. */
    public static void runeGlyph(Canvas c, int cx, int cy) {
        diamond(c, cx, cy, 4, 0xFFB8913E);
        diamond(c, cx, cy, 3, Theme.GOLD_BRIGHT);
        diamond(c, cx, cy, 1, 0xFF6B4E1E);
    }

    public static List<String> wrap(Canvas c, String text, int width) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.split("\n")) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String candidate = line.length() == 0 ? word : line + " " + word;
                if (c.textWidth(candidate) > width && line.length() > 0) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(candidate);
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    /** Draws wrapped text and returns the y below it. */
    public static int paragraph(Canvas c, String text, int x, int y, int width, int color) {
        for (String line : wrap(c, text, width)) {
            c.text(line, x, y, color, false);
            y += 10;
        }
        return y;
    }

    /** Label on the left, value right-aligned, with a dotted leader. */
    public static void statRow(Canvas c, int x, int y, int w, String label, String value, int labelColor, int valueColor) {
        c.text(label, x, y, labelColor, false);
        right(c, value, x + w, y, valueColor, false);
    }

    public static float clamp01(float v) {
        return v < 0 ? 0 : Math.min(1, v);
    }

    public static float ease(float t) {
        t = clamp01(t);
        return t * t * (3 - 2 * t);
    }

    private Ui() {
    }
}
