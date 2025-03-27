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

import com.naizo.finetuned.world.inventory.BookPage6Menu;
import com.naizo.finetuned.network.BookPage6ButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class BookPage6Screen extends AbstractContainerScreen<BookPage6Menu> {
	private final static HashMap<String, Object> guistate = BookPage6Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_arrow_back;

	public BookPage6Screen(BookPage6Menu container, Inventory inventory, Component text) {
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
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/finetunned_book_page_1.png"), this.leftPos + -60, this.topPos + -20, 0, 0, 300, 200, 300, 200);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_weapons"), 0, -8, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_materials"), 140, -10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_earthly_ingot"), -35, 19, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_iron"), -34, 35, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_diamond"), -34, 49, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1_x_block_of_coal"), -34, 62, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_obsidian_ingot"), -34, 80, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_iron1"), -33, 97, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_diamond1"), -32, 112, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_block_of_crying_obsidian"), -32, 127, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_rose_gold_ingot"), 111, 19, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_iron2"), 112, 36, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_diamond2"), 112, 51, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_1x_block_of_gold"), 113, 65, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_require_a_hammer"), 117, 126, -10092544, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_6.label_all_recipes"), 134, 114, -10092544, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_arrow_back = new ImageButton(this.leftPos + -75, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow_back.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage6ButtonMessage(0, x, y, z));
				BookPage6ButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow_back", imagebutton_arrow_back);
		this.addRenderableWidget(imagebutton_arrow_back);
	}
}
