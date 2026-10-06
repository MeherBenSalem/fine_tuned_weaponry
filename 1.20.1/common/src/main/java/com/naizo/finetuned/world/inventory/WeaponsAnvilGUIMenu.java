package com.naizo.finetuned.world.inventory;

import com.naizo.finetuned.init.FineTunedWeaponryModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import com.naizo.finetuned.util.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WeaponsAnvilGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public static final HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private Container internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private BlockEntity boundBlockEntity = null;

	public WeaponsAnvilGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(FineTunedWeaponryModMenus.WEAPONS_ANVIL_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new SimpleContainer(7);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
			boundBlockEntity = this.world.getBlockEntity(pos);
			if (boundBlockEntity instanceof Container container) {
				this.internal = container;
				this.bound = true;
			}
		}
		addTaggedSlot(0, 24, 34, stack -> stack.is(ModTags.ANVIL_TOOLS));
		addTaggedSlot(1, 67, 18, stack -> stack.is(ModTags.GEM));
		addTaggedSlot(2, 67, 48, stack -> stack.is(ModTags.GEM));
		addTaggedSlot(3, 104, 17, stack -> stack.is(ModTags.AMP));
		addTaggedSlot(4, 104, 48, stack -> stack.is(ModTags.AMP));
		addTaggedSlot(5, 140, 18, stack -> stack.is(ModTags.AMP));
		addTaggedSlot(6, 140, 48, stack -> stack.is(ModTags.AMP));
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 1 + 8 + sj * 18, -1 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 1 + 8 + si * 18, -1 + 142));
	}

	private void addTaggedSlot(int index, int posX, int posY, java.util.function.Predicate<ItemStack> predicate) {
		this.customSlots.put(index, this.addSlot(new Slot(internal, index, posX, posY) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return predicate.test(stack);
			}
		}));
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound && this.boundBlockEntity != null) {
			return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 7) {
				if (!this.moveItemStackTo(itemstack1, 7, this.slots.size(), true))
					return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(itemstack1, 0, 7, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
