package io.nightbeam.runeborn.core.ui;

import java.util.List;

/**
 * Drawing surface implemented once per Minecraft version. Coordinates are GUI-scaled pixels,
 * colors are ARGB. Strings may contain legacy {@code §} formatting codes.
 */
public interface Canvas {
    int width();

    int height();

    void fill(int x0, int y0, int x1, int y1, int argb);

    /** Vertical gradient, top color to bottom color. */
    void gradient(int x0, int y0, int x1, int y1, int top, int bottom);

    /** Body text in the vanilla font. */
    void text(String s, int x, int y, int argb, boolean shadow);

    int textWidth(String s);

    /** Display text in the serif title font, scaled. */
    void title(String s, float x, float y, int argb, float scale);

    /** Width of title text at scale 1. */
    int titleWidth(String s);

    /** Renders a registered item by id ({@code runeborn:foo} or {@code minecraft:bar}). */
    void item(String itemId, int x, int y);

    /** Renders the player's inventory stack in {@code slot} (0-8 hotbar, 40 offhand), with count. */
    void slot(int slot, int x, int y);

    void push();

    void pop();

    void translate(float x, float y);

    void scale(float s);

    void scissor(int x0, int y0, int x1, int y1);

    void endScissor();

    /** Translates a key, formatting {@code %s} arguments. Falls back to English. */
    String tr(String key, Object... args);

    /** Vanilla-style tooltip box at the mouse. */
    void tooltip(List<String> lines, int x, int y);

    /** Full item tooltip for a registered item id. */
    void itemTooltip(String itemId, int x, int y);

    /** Full item tooltip for the stack in an inventory slot. */
    void slotTooltip(int slot, int x, int y);

    /** Game time in ticks plus partial tick, for animation. */
    float time();
}
