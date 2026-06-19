
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

import com.naizo.finetuned.procedures.InosukesswordLivingEntityIsHitWithToolProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class InosukesswordItem extends SwordItem {
	public InosukesswordItem() {
		super(new Tier() {
			public int getUses() {
				return 3500;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 10f;
			}

			public int getLevel() {
				return 2;
			}

			public int getEnchantmentValue() {
				return 15;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(FineTunedWeaponryModItems.EARTHLY_INGOT.get()));
			}
		}, 3, -2.2f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		InosukesswordLivingEntityIsHitWithToolProcedure.execute(entity.level(), entity);
		return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.inosukessword.description_0"));
		list.add(Component.translatable("item.fine_tuned_weaponry.inosukessword.description_1"));
		list.add(Component.translatable("item.fine_tuned_weaponry.inosukessword.description_2"));
		list.add(Component.translatable("item.fine_tuned_weaponry.inosukessword.description_3"));
	}
}
