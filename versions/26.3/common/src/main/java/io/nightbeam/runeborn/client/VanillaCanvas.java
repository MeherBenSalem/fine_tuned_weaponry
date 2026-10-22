package io.nightbeam.runeborn.client;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.client.ClientNet;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.ui.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** {@link Canvas} over 26.3's {@link GuiGraphicsExtractor}. */
public final class VanillaCanvas implements Canvas {
    private static final Style TITLE = Style.EMPTY.withFont(new FontDescription.Resource(Compat.id("title")));
    private static final Map<String, ItemStack> ICONS = new HashMap<>();

    private final GuiGraphicsExtractor g;
    private final Font font;
    private final float partial;

    public VanillaCanvas(GuiGraphicsExtractor g, float partial) {
        this.g = g;
        this.font = Minecraft.getInstance().font;
        this.partial = partial;
    }

    @Override
    public int width() {
        return g.guiWidth();
    }

    @Override
    public int height() {
        return g.guiHeight();
    }

    @Override
    public void fill(int x0, int y0, int x1, int y1, int argb) {
        g.fill(x0, y0, x1, y1, argb);
    }

    @Override
    public void gradient(int x0, int y0, int x1, int y1, int top, int bottom) {
        g.fillGradient(x0, y0, x1, y1, top, bottom);
    }

    @Override
    public void text(String s, int x, int y, int argb, boolean shadow) {
        g.text(font, s, x, y, argb, shadow);
    }

    @Override
    public int textWidth(String s) {
        return font.width(s);
    }

    @Override
    public void title(String s, float x, float y, int argb, float scale) {
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);
        g.text(font, Component.literal(s).withStyle(TITLE), 0, 0, argb, false);
        g.pose().popMatrix();
    }

    @Override
    public int titleWidth(String s) {
        return font.width(Component.literal(s).withStyle(TITLE));
    }

    private static ItemStack icon(String id) {
        return ICONS.computeIfAbsent(id, key -> {
            Identifier loc = Identifier.tryParse(key);
            return loc == null ? ItemStack.EMPTY : new ItemStack(BuiltInRegistries.ITEM.getValue(loc));
        });
    }

    @Override
    public void item(String itemId, int x, int y) {
        ItemStack stack = icon(itemId);
        if (!stack.isEmpty()) {
            g.item(stack, x, y);
        }
    }

    private static ItemStack slotStack(int slot) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return ItemStack.EMPTY;
        }
        return slot < 0 ? player.getMainHandItem() : player.getInventory().getItem(slot);
    }

    @Override
    public void slot(int slot, int x, int y) {
        ItemStack stack = slotStack(slot);
        if (!stack.isEmpty()) {
            g.item(stack, x, y);
            g.itemDecorations(font, stack, x, y);
        }
    }

    @Override
    public void push() {
        g.pose().pushMatrix();
    }

    @Override
    public void pop() {
        g.pose().popMatrix();
    }

    @Override
    public void translate(float x, float y) {
        g.pose().translate(x, y);
    }

    @Override
    public void scale(float s) {
        g.pose().scale(s, s);
    }

    @Override
    public void scissor(int x0, int y0, int x1, int y1) {
        g.enableScissor(x0, y0, x1, y1);
    }

    @Override
    public void endScissor() {
        g.disableScissor();
    }

    @Override
    public String tr(String key, Object... args) {
        return translate(key, args);
    }

    public static String translate(String key, Object... args) {
        if (Language.getInstance().has(key)) {
            return I18n.get(key, args);
        }
        String en = Lang.en(key);
        return args.length == 0 ? en : String.format(en, args);
    }

    @Override
    public void tooltip(List<String> lines, int x, int y) {
        List<Component> list = new ArrayList<>(lines.size());
        for (String line : lines) {
            list.add(Component.literal(line));
        }
        g.setComponentTooltipForNextFrame(font, list, x, y);
    }

    @Override
    public void itemTooltip(String itemId, int x, int y) {
        ItemStack stack = icon(itemId);
        if (!stack.isEmpty()) {
            g.setTooltipForNextFrame(font, stack, x, y);
        }
    }

    @Override
    public void slotTooltip(int slot, int x, int y) {
        ItemStack stack = slotStack(slot);
        if (!stack.isEmpty()) {
            g.setTooltipForNextFrame(font, stack, x, y);
        }
    }

    @Override
    public float time() {
        return ClientNet.clock + partial;
    }
}
