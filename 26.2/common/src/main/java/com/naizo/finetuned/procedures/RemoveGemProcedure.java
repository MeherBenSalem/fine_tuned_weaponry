package com.naizo.finetuned.procedures;


import com.naizo.finetuned.util.GemNbtKeys;
import com.naizo.finetuned.util.ItemStackDataHelper;
import com.naizo.finetuned.util.RegistryHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;
import java.util.Map;

public class RemoveGemProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		int count = 0;
		if (new Object() {
			public int getAmount(int sltid) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
					if (stack != null)
						return stack.getCount();
				}
				return 0;
			}
		}.getAmount(0) > 0) {
			count = 1;
			for (int index0 = 0; index0 < 6; index0++) {
				if (!GemNbtKeys.getSocketItemId(entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY, count).isEmpty()) {
					if (new Object() {
						public int getAmount(int sltid) {
							if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
								ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
								if (stack != null)
									return stack.getCount();
							}
							return 0;
						}
					}.getAmount((int) count) == 0) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(RegistryHelper.getItem(GemNbtKeys.getSocketItemId(entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY, count).toLowerCase(java.util.Locale.ENGLISH)))
									.copy();
							_setstack.setCount(1);
							((Slot) _slots.get((int) count)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
						ItemStack weaponSlot = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY;
						final int slotIndex = (int) count;
						GemNbtKeys.clearSocket(weaponSlot, slotIndex);
						ItemStack removedGem = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(slotIndex)).getItem() : ItemStack.EMPTY;
						ItemStackDataHelper.updateTag(weaponSlot, tag -> tag.putBoolean((net.minecraft.core.registries.BuiltInRegistries.ITEM
								.getKey(removedGem.getItem())
								.toString()), false));
					} else {
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal(("Please remove the item from slot " + count)), false);
					}
				}
				count = count + 1;
			}
			ItemStack weapon = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY;
			ItemStackDataHelper.updateTag(weapon, tag -> {
				tag.putBoolean(GemNbtKeys.MODIFIED, false);
				tag.remove(GemNbtKeys.LEGACY_MODIFIED);
			});
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Insert Item First"), false);
		}
	}
}
