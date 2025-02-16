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

import com.naizo.finetuned.world.inventory.WeaponsForgeGUIMenu;
import com.naizo.finetuned.procedures.WeaponForgeReturnTimeProcedure;
import com.naizo.finetuned.network.WeaponsForgeGUIButtonMessage;
import com.naizo.finetuned.FineTunedWeaponryMod;

import com.mojang.blaze3d.systems.RenderSystem;

public class WeaponsForgeGUIScreen extends AbstractContainerScreen<WeaponsForgeGUIMenu> {
	private final static HashMap<String, Object> guistate = WeaponsForgeGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_forge_button;

	public WeaponsForgeGUIScreen(WeaponsForgeGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("fine_tuned_weaponry:textures/screens/weapons_forge_gui.png");

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

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/hammer_icon.png"), this.leftPos + 105, this.topPos + 26, 0, 0, 16, 16, 16, 16);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/background_forge.png"), this.leftPos + 105, this.topPos + 6, 0, 0, 64, 64, 64, 64);

		guiGraphics.blit(new ResourceLocation("fine_tuned_weaponry:textures/screens/plus_icon.png"), this.leftPos + 66, this.topPos + 30, 0, 0, 8, 8, 8, 8);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.fine_tuned_weaponry.weapons_forge_gui.label_forge"), 53, 5, -12829636, false);
		guiGraphics.drawString(this.font,

				WeaponForgeReturnTimeProcedure.execute(world, x, y, z), 128, 71, -16777216, false);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_forge_button = new ImageButton(this.leftPos + 80, this.topPos + 48, 18, 18, 0, 0, 18, new ResourceLocation("fine_tuned_weaponry:textures/screens/atlas/imagebutton_forge_button.png"), 18, 36, e -> {
			if (true) {
				FineTunedWeaponryMod.PACKET_HANDLER.sendToServer(new WeaponsForgeGUIButtonMessage(0, x, y, z));
				WeaponsForgeGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_forge_button", imagebutton_forge_button);
		this.addRenderableWidget(imagebutton_forge_button);
	}
}
