
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

import com.naizo.finetuned.procedures.RosegoldhammerItemIsCraftedsmeltedProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class RosegoldhammerItem extends SwordItem {
	public RosegoldhammerItem() {
		super(new Tier() {
			public int getUses() {
				return 1500;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 9f;
			}

			public int getLevel() {
				return 2;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(FineTunedWeaponryModItems.ROSE_GOLD_INGOT.get()));
			}
		}, 3, -3f, new Item.Properties());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.fine_tuned_weaponry.rosegoldhammer.description_0"));
	}

	@Override
	public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
		super.onCraftedBy(itemstack, world, entity);
		RosegoldhammerItemIsCraftedsmeltedProcedure.execute(world, itemstack);
	}
}
