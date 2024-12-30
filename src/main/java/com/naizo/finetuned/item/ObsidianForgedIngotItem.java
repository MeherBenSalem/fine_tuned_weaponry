
package com.naizo.finetuned.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class ObsidianForgedIngotItem extends Item {
	public ObsidianForgedIngotItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}
}
