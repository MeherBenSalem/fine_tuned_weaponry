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

import com.naizo.finetuned.world.inventory.ResearchTableUIMenu;
import com.naizo.finetuned.procedures.IsItemThereProcedure;
import com.naizo.finetuned.procedures.InsetSwordWeaponNameProcedure;
import com.naizo.finetuned.network.ResearchTableUIButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class ResearchTableUIScreen extends AbstractContainerScreen<ResearchTableUIMenu> {
	private final static HashMap<String, Object> guistate = ResearchTableUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_button_up;
	ImageButton imagebutton_button_down;

	public ResearchTableUIScreen(ResearchTableUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 177;
		this.imageHeight = 165;
	}

	private static final ResourceLocation texture = new ResourceLocation("fine_tuned_weaponry:textures/screens/research_table_ui.png");

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

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/book.png"), this.leftPos + 1, this.topPos + 1, 0, 0, 175, 80, 175, 80);

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
		guiGraphics.drawString(this.font,

				InsetSwordWeaponNameProcedure.execute(entity), 54, -16, -1, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_button_up = new ImageButton(this.leftPos + 22, this.topPos + 54, 9, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_button_up.png"), 9, 20, e -> {
			if (IsItemThereProcedure.execute(entity)) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new ResearchTableUIButtonMessage(0, x, y, z));
				ResearchTableUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = IsItemThereProcedure.execute(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_button_up", imagebutton_button_up);
		this.addRenderableWidget(imagebutton_button_up);
		imagebutton_button_down = new ImageButton(this.leftPos + 33, this.topPos + 54, 9, 10, 0, 0, 10, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_button_down.png"), 9, 20, e -> {
			if (IsItemThereProcedure.execute(entity)) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new ResearchTableUIButtonMessage(1, x, y, z));
				ResearchTableUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int gx, int gy, float ticks) {
				this.visible = IsItemThereProcedure.execute(entity);
				super.renderWidget(guiGraphics, gx, gy, ticks);
			}
		};
		guistate.put("button:imagebutton_button_down", imagebutton_button_down);
		this.addRenderableWidget(imagebutton_button_down);
	}
}
