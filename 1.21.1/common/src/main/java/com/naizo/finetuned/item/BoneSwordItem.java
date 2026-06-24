
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;

import java.util.List;

import com.naizo.finetuned.util.WeaponTierHelper;
import com.naizo.finetuned.procedures.BoneSwordLivingEntityIsHitWithToolProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class BoneSwordItem extends SwordItem {
	private static final Tier TIER = WeaponTierHelper.create(3800, 4f, 11f, 3, 15, Ingredient.of(new ItemStack(FineTunedWeaponryModItems.EARTHLY_INGOT.get())));

	public BoneSwordItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -2f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		BoneSwordLivingEntityIsHitWithToolProcedure.execute(entity);
		return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.bone_sword.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.bone_sword.description_1"));
		list.add(Component.translatable("item.fine_tuned_weaponry.bone_sword.description_2"));
	}
}
