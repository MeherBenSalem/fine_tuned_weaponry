package com.naizo.finetuned.util;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class GemNbtKeys {
	public static final String MODIFIED = "fine_tuned_weaponry_modified";
	/** @deprecated use {@link #MODIFIED}; kept for reading legacy item data */
	public static final String LEGACY_MODIFIED = "finetunned";

	public static final String INFERNO_CORE = key(FineTunedWeaponryModItems.INFERNO_CORE.get());
	public static final String FROST_RUNE = key(FineTunedWeaponryModItems.FROST_RUNE.get());
	public static final String STORM_SHARD = key(FineTunedWeaponryModItems.STORM_SHARD.get());
	public static final String BLAZING_AMPLIFIER = key(FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get());
	public static final String WILDFIRE_AMPLIFIER = key(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get());
	public static final String HELLFIRE_AMPLIFIER = key(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get());
	public static final String CHARGED_AMPLIFIER = key(FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get());
	public static final String OVERLOAD_AMPLIFIER = key(FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER.get());
	public static final String SUPERSTORM_AMPLIFIER = key(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER.get());
	public static final String GLACIAL_AMPLIFIER = key(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER.get());
	public static final String PERMAFROST_AMPLIFIER = key(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER.get());
	public static final String BLIZZARD_AMPLIFIER = key(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER.get());
	public static final String PYROCLASM_AMPLIFIER = key(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get());
	public static final String INFERNAL_HUNGER_AMPLIFIER = key(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get());
	public static final String LAVA_INFUSION_AMPLIFIER = key(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get());
	public static final String SMOKESCREEN_AMPLIFIER = key(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get());
	public static final String VOLCANIC_BURST_AMPLIFIER = key(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get());
	public static final String STORM_FURY_AMPLIFIER = key(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER.get());
	public static final String STATIC_AMPLIFIER = key(FineTunedWeaponryModItems.STATIC_AMPLIFIER.get());

	private GemNbtKeys() {
	}

	public static String key(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	public static String getSocketItemId(ItemStack stack, int slot) {
		var tag = ItemStackDataHelper.getTag(stack);
		if (tag == null) {
			return "";
		}
		String itemId = tag.getString("ft_slot" + slot);
		// Older releases used a double as the socket counter.
		return itemId.isEmpty() ? tag.getString("ft_slot" + slot + ".0") : itemId;
	}

	public static void clearSocket(ItemStack stack, int slot) {
		ItemStackDataHelper.updateTag(stack, tag -> {
			tag.putString("ft_slot" + slot, "");
			tag.remove("ft_slot" + slot + ".0");
		});
	}

	public static boolean isModified(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		var tag = ItemStackDataHelper.getTag(stack);
		if (tag == null) {
			return false;
		}
		return tag.getBoolean(MODIFIED) || tag.getBoolean(LEGACY_MODIFIED);
	}

	public static boolean hasGem(ItemStack stack, String gemKey) {
		if (stack.isEmpty()) {
			return false;
		}
		var tag = ItemStackDataHelper.getTag(stack);
		return tag != null && tag.getBoolean(gemKey);
	}

	public static boolean hasAnyAttackGem(ItemStack stack) {
		if (!isModified(stack)) {
			return false;
		}
		return hasGem(stack, INFERNO_CORE) || hasGem(stack, FROST_RUNE) || hasGem(stack, STORM_SHARD);
	}

	public static void markModified(ItemStack stack) {
		ItemStackDataHelper.updateTag(stack, tag -> {
			tag.putBoolean(MODIFIED, true);
			tag.remove(LEGACY_MODIFIED);
		});
	}

	public static void socketGem(ItemStack weapon, Item gem) {
		String key = key(gem);
		ItemStackDataHelper.updateTag(weapon, tag -> tag.putBoolean(key, true));
		markModified(weapon);
	}
}
