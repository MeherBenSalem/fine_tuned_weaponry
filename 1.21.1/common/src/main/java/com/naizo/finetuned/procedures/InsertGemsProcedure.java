package com.naizo.finetuned.procedures;

import com.naizo.finetuned.init.FineTunedWeaponryModSounds;
import com.naizo.finetuned.util.GemNbtKeys;
import com.naizo.finetuned.util.ItemStackDataHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Map;
import java.util.function.Supplier;

public class InsertGemsProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
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
				if (new Object() {
					public int getAmount(int sltid) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
							if (stack != null)
								return stack.getCount();
						}
						return 0;
					}
				}.getAmount((int) count) > 0) {
					ItemStack weaponCheck = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY;
					if (!GemNbtKeys.getSocketItemId(weaponCheck, count).isEmpty()) {
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal(("Slot " + count + " already contains a modification")), false);
					} else {
						ItemStack weapon = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY;
						ItemStack gemStack = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get((int) count)).getItem() : ItemStack.EMPTY;
						final int slotIndex = (int) count;
						ItemStackDataHelper.updateTag(weapon, tag -> tag.putString(("ft_slot" + slotIndex), GemNbtKeys.key(gemStack.getItem())));
						GemNbtKeys.socketGem(weapon, gemStack.getItem());
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							((Slot) _slots.get((int) count)).remove(1);
							_player.containerMenu.broadcastChanges();
						}
					}
				}
				count = count + 1;
			}
			ItemStack weapon = entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY;
			GemNbtKeys.markModified(weapon);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), FineTunedWeaponryModSounds.ACTIVATION_SOUND_EFFECT.get(), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, FineTunedWeaponryModSounds.ACTIVATION_SOUND_EFFECT.get(), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Insert Item First"), false);
		}
	}
}
