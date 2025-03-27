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

import com.naizo.finetuned.world.inventory.ResearchTableGUIMenu;
import com.naizo.finetuned.network.ResearchTableGUIButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class ResearchTableGUIScreen extends AbstractContainerScreen<ResearchTableGUIMenu> {
	private final static HashMap<String, Object> guistate = ResearchTableGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_buttonarrow;

	public ResearchTableGUIScreen(ResearchTableGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("fine_tuned_weaponry:textures/screens/research_table_gui.png");

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
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/bookcreate.png"), this.leftPos + 17, this.topPos + 9, 0, 0, 140, 64, 140, 64);

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
		imagebutton_buttonarrow = new ImageButton(this.leftPos + 91, this.topPos + 33, 17, 16, 0, 0, 16, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_buttonarrow.png"), 17, 32, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new ResearchTableGUIButtonMessage(0, x, y, z));
				ResearchTableGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_buttonarrow", imagebutton_buttonarrow);
		this.addRenderableWidget(imagebutton_buttonarrow);
	}
}
