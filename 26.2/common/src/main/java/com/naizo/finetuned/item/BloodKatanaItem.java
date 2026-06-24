
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;
import com.naizo.finetuned.procedures.BloodKatanaRightClickedProcedure;
import com.naizo.finetuned.procedures.BloodKatanaLivingEntityIsHitWithToolProcedure;

public class BloodKatanaItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(300, 4f, 11f, 2, 15, Ingredient.of());

	public BloodKatanaItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -2.2f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		BloodKatanaLivingEntityIsHitWithToolProcedure.execute(sourceentity);
		return retval;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		BloodKatanaRightClickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.blood_katana.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.blood_katana.description_1"));
		list.add(Component.translatable("item.fine_tuned_weaponry.blood_katana.description_2"));
	}

	@Override	public boolean isFoil(ItemStack itemstack) {
		return true;
	}
}
