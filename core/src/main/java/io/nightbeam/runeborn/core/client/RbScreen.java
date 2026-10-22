package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.ui.Canvas;
import io.nightbeam.runeborn.core.ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Version-independent screen. Each Minecraft version wraps it in a thin vanilla {@code Screen}
 * that forwards rendering and input here.
 *
 * <p>Screens register clickable regions while rendering ({@link #hot}); clicks are matched
 * against the regions from the last frame, so layout code lives in one place.
 */
public abstract class RbScreen {
    protected ClientHost host;
    protected int width;
    protected int height;
    private final List<Hotspot> hotspots = new ArrayList<>();

    private record Hotspot(int x, int y, int w, int h, IntConsumer action) {
    }

    /** Registers a clickable region; the action receives the mouse button. */
    protected void hot(int x, int y, int w, int h, IntConsumer action) {
        hotspots.add(new Hotspot(x, y, w, h, action));
    }

    protected void hot(int x, int y, int w, int h, Runnable action) {
        hotspots.add(new Hotspot(x, y, w, h, b -> action.run()));
    }

    protected void clearHotspots() {
        hotspots.clear();
    }

    /** Dispatches to the topmost region under the mouse. */
    protected boolean clickHotspot(double mx, double my, int button) {
        for (int i = hotspots.size() - 1; i >= 0; i--) {
            Hotspot h = hotspots.get(i);
            if (Ui.hit(mx, my, h.x, h.y, h.w, h.h)) {
                click();
                h.action.accept(button);
                return true;
            }
        }
        return false;
    }

    public final void attach(ClientHost host, int width, int height) {
        this.host = host;
        this.width = width;
        this.height = height;
        init();
    }

    protected void init() {
    }

    public abstract void render(Canvas c, int mouseX, int mouseY, float partial);

    public boolean mouseClicked(double mx, double my, int button) {
        return false;
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        return false;
    }

    /** GLFW key codes. Return true to consume. */
    public boolean keyPressed(int key) {
        return false;
    }

    public void tick() {
    }

    public boolean closeOnEscape() {
        return true;
    }

    public void onClose() {
    }

    public abstract String titleKey();

    protected void click() {
        host.playClick();
    }
}
