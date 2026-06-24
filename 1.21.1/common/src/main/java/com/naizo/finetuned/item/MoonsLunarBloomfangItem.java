
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;
import com.naizo.finetuned.procedures.MoonsLunarBloomfangRightclickedProcedure;

public class MoonsLunarBloomfangItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(350, 4f, 11f, 0, 15, Ingredient.of(new ItemStack(Items.IRON_INGOT)));

	public MoonsLunarBloomfangItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -1.5f)));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		MoonsLunarBloomfangRightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.moons_lunar_bloomfang.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.moons_lunar_bloomfang.description_1"));
		list.add(Component.translatable("item.fine_tuned_weaponry.moons_lunar_bloomfang.description_2"));
	}
}
