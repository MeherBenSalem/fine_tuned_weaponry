package com.naizo.finetuned.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.naizo.finetuned.world.inventory.BookPage5Menu;
import com.naizo.finetuned.network.BookPage5ButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class BookPage5Screen extends AbstractContainerScreen<BookPage5Menu> {
	private final static HashMap<String, Object> guistate = BookPage5Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_arrow_back;
	ImageButton imagebutton_arrow;

	public BookPage5Screen(BookPage5Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 15 && mouseY > topPos + 26 && mouseY < topPos + 34)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_iron_block"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 8 && mouseY > topPos + 34 && mouseY < topPos + 41)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_stick"), mouseX, mouseY);
		if (mouseX > leftPos + 37 && mouseX < leftPos + 47 && mouseY > topPos + 32 && mouseY < topPos + 42)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_classic_hammer"), mouseX, mouseY);
		if (mouseX > leftPos + 16 && mouseX < leftPos + 22 && mouseY > topPos + 26 && mouseY < topPos + 33)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_weapons_template3"), mouseX, mouseY);
		if (mouseX > leftPos + 8 && mouseX < leftPos + 16 && mouseY > topPos + 40 && mouseY < topPos + 47)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_iron_sword"), mouseX, mouseY);
		if (mouseX > leftPos + 137 && mouseX < leftPos + 159 && mouseY > topPos + 41 && mouseY < topPos + 48)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_stone"), mouseX, mouseY);
		if (mouseX > leftPos + 173 && mouseX < leftPos + 182 && mouseY > topPos + 32 && mouseY < topPos + 42)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_forge"), mouseX, mouseY);
		if (mouseX > leftPos + 145 && mouseX < leftPos + 152 && mouseY > topPos + 34 && mouseY < topPos + 41)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_charcoal"), mouseX, mouseY);
		if (mouseX > leftPos + 138 && mouseX < leftPos + 145 && mouseY > topPos + 34 && mouseY < topPos + 40)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_stone1"), mouseX, mouseY);
		if (mouseX > leftPos + 152 && mouseX < leftPos + 159 && mouseY > topPos + 34 && mouseY < topPos + 40)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.tooltip_stone2"), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/finetunned_book_page_1.png"), this.leftPos + -60, this.topPos + -20, 0, 0, 300, 200, 300, 200);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/sythe_rec.png"), this.leftPos + 0, this.topPos + 25, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/forge_rec.png"), this.leftPos + 136, this.topPos + 25, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/unlit_forge.png"), this.leftPos + 109, this.topPos + 107, 0, 0, 31, 25, 31, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/lit_forge_rec.png"), this.leftPos + 178, this.topPos + 107, 0, 0, 28, 25, 28, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/hammer_icon.png"), this.leftPos + 151, this.topPos + 110, 0, 0, 16, 16, 16, 16);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_weapons"), -10, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_classic_hammer"), -11, 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_materials"), 147, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_forge_recipe"), 130, 11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_craft_a_forge_first"), 101, 61, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_a_block_of_coal"), 101, 75, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_5.label_ignite_it_for_120_secs"), 100, 89, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_arrow_back = new ImageButton(this.leftPos + -75, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow_back.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage5ButtonMessage(0, x, y, z));
				BookPage5ButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow_back", imagebutton_arrow_back);
		this.addRenderableWidget(imagebutton_arrow_back);
		imagebutton_arrow = new ImageButton(this.leftPos + 247, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage5ButtonMessage(1, x, y, z));
				BookPage5ButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow", imagebutton_arrow);
		this.addRenderableWidget(imagebutton_arrow);
	}
}
