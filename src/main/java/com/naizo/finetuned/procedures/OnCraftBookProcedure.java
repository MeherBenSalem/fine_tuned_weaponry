package com.naizo.finetuned.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.Map;

import com.naizo.finetuned.init.FineTunedWeaponryModItems;

public class OnCraftBookProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (new Object() {
			public int getAmount(int sltid) {
				if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
					ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
					if (stack != null)
						return stack.getCount();
				}
				return 0;
			}
		}.getAmount(3) == 0) {
			if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(0)).getItem() : ItemStack.EMPTY).getItem() == Items.BOOK) {
				if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(1)).getItem() : ItemStack.EMPTY).getItem() == Items.FIRE_CHARGE) {
					if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.TORCH) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.BLAZING_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.REDSTONE) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.WILDFIRE_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.CAMPFIRE
							.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.HELLFIRE_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.TNT.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.PYROCLASM_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.GOLDEN_APPLE) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.INFERNAL_HUNGER_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY)
							.getItem() == Items.POISONOUS_POTATO) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.LAVA_INFUSION_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.SAND.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.SMOKESCREEN_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.LAVA_BUCKET) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.VOLCANIC_BURST_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
				} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(1)).getItem() : ItemStack.EMPTY).getItem() == Blocks.ICE.asItem()) {
					if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.ICE.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.GLACIAL_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.SNOW_BLOCK
							.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.PERMAFROST_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.PACKED_ICE
							.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.BLIZZARD_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
				} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(1)).getItem() : ItemStack.EMPTY).getItem() == Items.COPPER_INGOT) {
					if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Blocks.LIGHTNING_ROD
							.asItem()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.CHARGED_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY)
							.getItem() == FineTunedWeaponryModItems.CLASSICHAMMER.get()) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.OVERLOAD_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.WATER_BUCKET) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.SUPERSTORM_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY)
							.getItem() == Items.EXPERIENCE_BOTTLE) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.STORM_FURY_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					} else if ((entity instanceof Player _plrSlotItem && _plrSlotItem.containerMenu instanceof Supplier _splr && _splr.get() instanceof Map _slt ? ((Slot) _slt.get(2)).getItem() : ItemStack.EMPTY).getItem() == Items.RABBIT_HIDE) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack _setstack = new ItemStack(FineTunedWeaponryModItems.STATIC_AMPLIFIER.get()).copy();
							_setstack.setCount(1);
							((Slot) _slots.get(3)).set(_setstack);
							_player.containerMenu.broadcastChanges();
						}
					}
				}
				if (new Object() {
					public int getAmount(int sltid) {
						if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
							ItemStack stack = ((Slot) _slots.get(sltid)).getItem();
							if (stack != null)
								return stack.getCount();
						}
						return 0;
					}
				}.getAmount(3) > 0) {
					if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
						((Slot) _slots.get(2)).remove(1);
						_player.containerMenu.broadcastChanges();
					}
					if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
						((Slot) _slots.get(1)).remove(1);
						_player.containerMenu.broadcastChanges();
					}
					if (entity instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
						((Slot) _slots.get(0)).remove(1);
						_player.containerMenu.broadcastChanges();
					}
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
				}
			}
		}
	}
}
