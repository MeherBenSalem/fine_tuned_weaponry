
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;
import com.naizo.finetuned.procedures.ZenitsusSwordRightclickedProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class ZenitsusSwordItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(3800, 4f, 16f, 3, 15, Ingredient.of(new ItemStack(FineTunedWeaponryModItems.OBSIDIAN_FORGED_INGOT.get())));

	public ZenitsusSwordItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -2f)));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ZenitsusSwordRightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.zenitsus_sword.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.zenitsus_sword.description_1"));
	}
}
