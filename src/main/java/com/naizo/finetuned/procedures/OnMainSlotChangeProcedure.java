package com.naizo.finetuned.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;
import java.util.Map;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class OnMainSlotChangeProcedure {
	public static void execute(Entity entity, double slot) {
		if (entity == null)
			return;
		ItemStack inputItem = ItemStack.EMPTY;
		double emptySlot = 0;
		double gimmer = 0;
		emptySlot = 1;
		if (!((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY).getItem() == ItemStack.EMPTY.getItem())) {
			if (!((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY).getOrCreateTag().getBoolean("ftw_en"))) {
				(entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY).getOrCreateTag().putBoolean("ftw_en",
						true);
			}
			if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY).getOrCreateTag()
					.getDouble("ftwp_gminer") > 0) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.MINE_FAVOR_GEM.get()).copy();
					_setstack.setCount((int) ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY)
							.getOrCreateTag().getDouble("ftwp_gminer")));
					((Slot) _slots.get((int) emptySlot)).set(_setstack);
					_player.containerMenu.broadcastChanges();
				}
				(entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) slot)).getItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("ftwp_gminer",
						0);
				emptySlot = emptySlot + 1;
			}
		}
	}
}
