package io.nightbeam.runeborn.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.nightbeam.runeborn.core.client.RbScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        inner.render(new VanillaCanvas(graphics, partialTick), mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return inner.mouseClicked(event.x(), event.y(), event.button()) || super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return inner.mouseScrolled(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (inner.keyPressed(glfwKey(event.key()))) {
            return true;
        }
        return super.keyPressed(event);
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

    /** Core screens use GLFW key codes; 26.3 reports SDL scancodes. Unmapped keys become -1. */
    private static int glfwKey(int key) {
        return switch (key) {
            case InputConstants.KEY_ESCAPE -> 256;
            case InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER -> 257;
            case InputConstants.KEY_TAB -> 258;
            case InputConstants.KEY_BACKSPACE -> 259;
            case InputConstants.KEY_RIGHT -> 262;
            case InputConstants.KEY_LEFT -> 263;
            case InputConstants.KEY_DOWN -> 264;
            case InputConstants.KEY_UP -> 265;
            case InputConstants.KEY_0 -> '0';
            default -> {
                if (key >= InputConstants.KEY_A && key <= InputConstants.KEY_Z) {
                    yield 'A' + key - InputConstants.KEY_A;
                }
                yield key >= InputConstants.KEY_1 && key <= InputConstants.KEY_9 ? '1' + key - InputConstants.KEY_1 : -1;
            }
        };
    }
}
