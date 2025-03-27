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

import com.naizo.finetuned.world.inventory.BookPage3Menu;
import com.naizo.finetuned.network.BookPage3ButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class BookPage3Screen extends AbstractContainerScreen<BookPage3Menu> {
	private final static HashMap<String, Object> guistate = BookPage3Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_arrow_back;
	ImageButton imagebutton_arrow;

	public BookPage3Screen(BookPage3Menu container, Inventory inventory, Component text) {
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
		if (mouseX > leftPos + 8 && mouseX < leftPos + 16 && mouseY > topPos + 26 && mouseY < topPos + 34)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_iron_block"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 8 && mouseY > topPos + 34 && mouseY < topPos + 41)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_stick"), mouseX, mouseY);
		if (mouseX > leftPos + 37 && mouseX < leftPos + 47 && mouseY > topPos + 32 && mouseY < topPos + 42)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_classic_hammer"), mouseX, mouseY);
		if (mouseX > leftPos + 9 && mouseX < leftPos + 16 && mouseY > topPos + 72 && mouseY < topPos + 87)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_earthly_ingot"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 8 && mouseY > topPos + 72 && mouseY < topPos + 80)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_weapons_template"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 8 && mouseY > topPos + 80 && mouseY < topPos + 88)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_classic_hammer1"), mouseX, mouseY);
		if (mouseX > leftPos + 9 && mouseX < leftPos + 16 && mouseY > topPos + 119 && mouseY < topPos + 133)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_obsidian_ingot"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 9 && mouseY > topPos + 126 && mouseY < topPos + 134)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_classic_hammer2"), mouseX, mouseY);
		if (mouseX > leftPos + 1 && mouseX < leftPos + 9 && mouseY > topPos + 118 && mouseY < topPos + 126)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_weapons_template1"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 151 && mouseY > topPos + 34 && mouseY < topPos + 41)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_classic_hammer3"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 151 && mouseY > topPos + 25 && mouseY < topPos + 34)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_weapons_template2"), mouseX, mouseY);
		if (mouseX > leftPos + 151 && mouseX < leftPos + 159 && mouseY > topPos + 26 && mouseY < topPos + 41)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_rose_gold_ingot"), mouseX, mouseY);
		if (mouseX > leftPos + 159 && mouseX < leftPos + 166 && mouseY > topPos + 80 && mouseY < topPos + 87)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_string"), mouseX, mouseY);
		if (mouseX > leftPos + 36 && mouseX < leftPos + 46 && mouseY > topPos + 78 && mouseY < topPos + 88)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_earthly_hammer"), mouseX, mouseY);
		if (mouseX > leftPos + 37 && mouseX < leftPos + 47 && mouseY > topPos + 124 && mouseY < topPos + 135)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_obsidian_hammer"), mouseX, mouseY);
		if (mouseX > leftPos + 180 && mouseX < leftPos + 190 && mouseY > topPos + 31 && mouseY < topPos + 43)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_rose_gold_hammer"), mouseX, mouseY);
		if (mouseX > leftPos + 180 && mouseX < leftPos + 191 && mouseY > topPos + 78 && mouseY < topPos + 89)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_ruyi_jingu_staff"), mouseX, mouseY);
		if (mouseX > leftPos + 159 && mouseX < leftPos + 166 && mouseY > topPos + 72 && mouseY < topPos + 79)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_wood"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 152 && mouseY > topPos + 87 && mouseY < topPos + 93)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_wood1"), mouseX, mouseY);
		if (mouseX > leftPos + 152 && mouseX < leftPos + 158 && mouseY > topPos + 80 && mouseY < topPos + 87)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_weapons_template3"), mouseX, mouseY);
		if (mouseX > leftPos + 152 && mouseX < leftPos + 160 && mouseY > topPos + 87 && mouseY < topPos + 94)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_string1"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 152 && mouseY > topPos + 80 && mouseY < topPos + 86)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_string2"), mouseX, mouseY);
		if (mouseX > leftPos + 152 && mouseX < leftPos + 159 && mouseY > topPos + 72 && mouseY < topPos + 79)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_string3"), mouseX, mouseY);
		if (mouseX > leftPos + 180 && mouseX < leftPos + 190 && mouseY > topPos + 124 && mouseY < topPos + 134)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_lunar_bloomfang"), mouseX, mouseY);
		if (mouseX > leftPos + 151 && mouseX < leftPos + 159 && mouseY > topPos + 126 && mouseY < topPos + 133)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_iron_sword"), mouseX, mouseY);
		if (mouseX > leftPos + 151 && mouseX < leftPos + 159 && mouseY > topPos + 133 && mouseY < topPos + 140)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_golden_apple"), mouseX, mouseY);
		if (mouseX > leftPos + 159 && mouseX < leftPos + 166 && mouseY > topPos + 126 && mouseY < topPos + 133)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_flower"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 152 && mouseY > topPos + 125 && mouseY < topPos + 133)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_flower1"), mouseX, mouseY);
		if (mouseX > leftPos + 159 && mouseX < leftPos + 166 && mouseY > topPos + 118 && mouseY < topPos + 126)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_bone"), mouseX, mouseY);
		if (mouseX > leftPos + 144 && mouseX < leftPos + 152 && mouseY > topPos + 133 && mouseY < topPos + 140)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_bone1"), mouseX, mouseY);
		if (mouseX > leftPos + 151 && mouseX < leftPos + 159 && mouseY > topPos + 118 && mouseY < topPos + 126)
			guiGraphics.renderTooltip(font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.tooltip_weapons_template4"), mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/finetunned_book_page_1.png"), this.leftPos + -57, this.topPos + -20, 0, 0, 300, 200, 300, 200);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/ham_rec.png"), this.leftPos + 0, this.topPos + 25, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/earth_ham_rec.png"), this.leftPos + 0, this.topPos + 71, 0, 0, 48, 25, 48, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/obsd_rec_ham.png"), this.leftPos + 0, this.topPos + 117, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/rosegold_ham_rec.png"), this.leftPos + 143, this.topPos + 25, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/woo_rec.png"), this.leftPos + 143, this.topPos + 71, 0, 0, 49, 25, 49, 25);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/pink_sw_rec.png"), this.leftPos + 143, this.topPos + 117, 0, 0, 49, 25, 49, 25);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_weapons"), -8, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_classic_hammer"), -11, 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_earthly_hammer"), -11, 57, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_obsidian_hammer"), -11, 103, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_rose_gold_hammer"), 126, 11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_ruyi_jingu_staff"), 125, 57, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_3.label_lunar_bloomfang"), 129, 103, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_arrow_back = new ImageButton(this.leftPos + -75, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow_back.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage3ButtonMessage(0, x, y, z));
				BookPage3ButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow_back", imagebutton_arrow_back);
		this.addRenderableWidget(imagebutton_arrow_back);
		imagebutton_arrow = new ImageButton(this.leftPos + 249, this.topPos + 71, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage3ButtonMessage(1, x, y, z));
				BookPage3ButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow", imagebutton_arrow);
		this.addRenderableWidget(imagebutton_arrow);
	}
}
