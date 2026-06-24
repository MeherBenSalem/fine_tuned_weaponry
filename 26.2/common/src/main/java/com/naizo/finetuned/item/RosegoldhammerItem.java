
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;
import com.naizo.finetuned.procedures.RosegoldhammerItemIsCraftedsmeltedProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class RosegoldhammerItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(1500, 4f, 9f, 2, 2, Ingredient.of(new ItemStack(FineTunedWeaponryModItems.ROSE_GOLD_INGOT.get())));

	public RosegoldhammerItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -3f)));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.rosegoldhammer.description_0"));
	}

	@Override
	public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
		super.onCraftedBy(itemstack, world, entity);
		RosegoldhammerItemIsCraftedsmeltedProcedure.execute(world, itemstack);
	}
}
