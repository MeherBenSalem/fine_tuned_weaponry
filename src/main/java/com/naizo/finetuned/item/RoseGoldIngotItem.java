
package com.naizo.finetuned.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class RoseGoldIngotItem extends Item {
	public RoseGoldIngotItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.EPIC));
	}
}