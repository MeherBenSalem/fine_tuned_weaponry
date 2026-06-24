package com.naizo.finetuned.util;

import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class WeaponTierHelper {
	private WeaponTierHelper() {
	}

	public static Tier create(int uses, float speed, float attackDamage, int miningLevel, int enchantmentValue, Ingredient repairIngredient) {
		return new Tier() {
			@Override
			public int getUses() {
				return uses;
			}

			@Override
			public float getSpeed() {
				return speed;
			}

			@Override
			public float getAttackDamageBonus() {
				return attackDamage;
			}

			@Override
			public TagKey<Block> getIncorrectBlocksForDrops() {
				return miningLevelTag(miningLevel);
			}

			@Override
			public int getEnchantmentValue() {
				return enchantmentValue;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return repairIngredient;
			}
		};
	}

	private static TagKey<Block> miningLevelTag(int miningLevel) {
		return switch (miningLevel) {
			case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
			case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
			case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
			case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
			default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
		};
	}
}
