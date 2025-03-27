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

import com.naizo.finetuned.world.inventory.BookPage2Menu;
import com.naizo.finetuned.network.BookPage2ButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class BookPage2Screen extends AbstractContainerScreen<BookPage2Menu> {
	private final static HashMap<String, Object> guistate = BookPage2Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_arrow_back;
	ImageButton imagebutton_arrow;

	public BookPage2Screen(BookPage2Menu container, Inventory inventory, Component text) {
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

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/finetunned_book_page_1.png"), this.leftPos + -57, this.topPos + -20, 0, 0, 300, 200, 300, 200);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_guide"), 15, -11, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_weapons"), -30, 16, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_blocks"), -30, 34, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_gems"), -30, 70, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_research_table"), -30, 52, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_gems1"), -30, 88, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.book_page_2.label_amplifiers"), -30, 106, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_arrow_back = new ImageButton(this.leftPos + -75, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow_back.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage2ButtonMessage(0, x, y, z));
				BookPage2ButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow_back", imagebutton_arrow_back);
		this.addRenderableWidget(imagebutton_arrow_back);
		imagebutton_arrow = new ImageButton(this.leftPos + 249, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookPage2ButtonMessage(1, x, y, z));
				BookPage2ButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow", imagebutton_arrow);
		this.addRenderableWidget(imagebutton_arrow);
	}
}
