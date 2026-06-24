
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;

public class ClassichammerItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(1500, 4f, 6f, 2, 15, Ingredient.of(new ItemStack(Items.IRON_INGOT)));

	public ClassichammerItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -3.2f)));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.classichammer.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.classichammer.description_1"));
	}
}
