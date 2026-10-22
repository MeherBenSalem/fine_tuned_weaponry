package io.nightbeam.runeborn.client;

import io.nightbeam.runeborn.core.client.RbScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Vanilla screen that hosts a version-independent {@link RbScreen}. */
public final class HostScreen extends Screen {
    private final RbScreen inner;

    public HostScreen(RbScreen inner) {
        super(Component.translatable(inner.titleKey()));
        this.inner = inner;
    }

    @Override
    protected void init() {
        inner.attach(RbClient.HOST, width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        inner.render(new VanillaCanvas(graphics, partialTick), mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return inner.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return inner.mouseScrolled(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (inner.keyPressed(keyCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        inner.tick();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return inner.closeOnEscape();
    }

    @Override
    public void onClose() {
        inner.onClose();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public RbScreen inner() {
        return inner;
    }
}
