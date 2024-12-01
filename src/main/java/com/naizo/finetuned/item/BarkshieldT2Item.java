
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class BarkshieldT2Item extends ShieldItem {
	public BarkshieldT2Item() {
		super(new Item.Properties().durability(607));
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(FineTunedWeaponryModItems.BARKSHIELD_T_2.get())).test(repairitem);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.literal("\u00A73Tier 2"));
	}
}
