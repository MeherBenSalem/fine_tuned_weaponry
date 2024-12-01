
package com.naizo.finetuned.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;

import com.naizo.finetuned.procedures.BarkPickaxeItemIsCraftedsmeltedProcedure;
import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class BarkPickaxeItem extends PickaxeItem {
	public BarkPickaxeItem() {
		super(new Tier() {
			public int getUses() {
				return 250;
			}

			public float getSpeed() {
				return 5f;
			}

			public float getAttackDamageBonus() {
				return 2f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 0;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(FineTunedWeaponryModItems.T_1_REFINED_CRYSTAL.get()), new ItemStack(FineTunedWeaponryModItems.BARK_PICKAXE.get()));
			}
		}, 1, -3f, new Item.Properties());
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		BarkPickaxeItemIsCraftedsmeltedProcedure.execute(itemstack);
	}
}
