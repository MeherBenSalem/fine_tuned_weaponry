package com.naizo.finetuned.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

import java.util.List;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import com.naizo.finetuned.configuration.KatanaConfigConfiguration;

@Mod.EventBusSubscriber
public class GiveKurasaiKatanaTooltipProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		execute(event, event.getItemStack(), event.getToolTip());
	}

	public static void execute(ItemStack itemstack, List<Component> tooltip) {
		execute(null, itemstack, tooltip);
	}

	private static void execute(@Nullable Event event, ItemStack itemstack, List<Component> tooltip) {
		if (tooltip == null)
			return;
		if (itemstack.getItem() == FineTunedWeaponryModItems.KURASAI_KATANA.get()) {
			tooltip.add(3, Component.literal(("\u00A7eRight-click : Summon " + Math.round((double) KatanaConfigConfiguration.KURASAI_KATANA_LIGHTNING_STRIKES.get()) + " lightning strike around you and gain Grants \u00A76Resistance "
					+ (double) KatanaConfigConfiguration.KURASAI_KATANA_BUFF_POWER.get() + " for " + Math.round((double) KatanaConfigConfiguration.KURASAI_KATANA_BUFF_DURATION.get() * 0.05) + " sec")));
			tooltip.add(3, Component.literal(("\u00A79Cooldown : " + (double) KatanaConfigConfiguration.KURASAI_KATANA_BUFF_CDR.get() * 0.05 + " sec")));
		}
	}
}
