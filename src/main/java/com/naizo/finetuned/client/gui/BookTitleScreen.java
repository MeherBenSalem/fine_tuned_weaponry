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

import com.naizo.finetuned.world.inventory.BookTitleMenu;
import com.naizo.finetuned.network.BookTitleButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class BookTitleScreen extends AbstractContainerScreen<BookTitleMenu> {
	private final static HashMap<String, Object> guistate = BookTitleMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_arrow;

	public BookTitleScreen(BookTitleMenu container, Inventory inventory, Component text) {
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

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/title.png"), this.leftPos + 15, this.topPos + -11, 0, 0, 150, 180, 150, 180);

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
	}

	@Override
	public void init() {
		super.init();
		imagebutton_arrow = new ImageButton(this.leftPos + 168, this.topPos + 70, 7, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_arrow.png"), 7, 20, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new BookTitleButtonMessage(0, x, y, z));
				BookTitleButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_arrow", imagebutton_arrow);
		this.addRenderableWidget(imagebutton_arrow);
	}
}
