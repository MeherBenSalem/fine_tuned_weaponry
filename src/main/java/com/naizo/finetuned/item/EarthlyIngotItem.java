
package com.naizo.finetuned.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class EarthlyIngotItem extends Item {
	public EarthlyIngotItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
