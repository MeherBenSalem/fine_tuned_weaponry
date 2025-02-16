
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import com.naizo.finetuned.item.ZenitsusSwordItem;
import com.naizo.finetuned.item.WeapontemplateItem;
import com.naizo.finetuned.item.WaraxeItem;
import com.naizo.finetuned.item.RosegoldhammerItem;
import com.naizo.finetuned.item.RoseGoldIngotItem;
import com.naizo.finetuned.item.ObsidianhammerItem;
import com.naizo.finetuned.item.ObsidianForgedIngotItem;
import com.naizo.finetuned.item.MoonsLunarBloomfangItem;
import com.naizo.finetuned.item.InosukesswordItem;
import com.naizo.finetuned.item.ImmortalScytheItem;
import com.naizo.finetuned.item.HollowManRuyiJinguStaffItem;
import com.naizo.finetuned.item.HelishSwordItem;
import com.naizo.finetuned.item.Gem1Item;
import com.naizo.finetuned.item.EarthlyhammerItem;
import com.naizo.finetuned.item.EarthlyIngotItem;
import com.naizo.finetuned.item.ClassichammerItem;
import com.naizo.finetuned.item.ClassicKatanaItem;
import com.naizo.finetuned.item.BoneSwordItem;
import com.naizo.finetuned.item.BloodKatanaItem;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Item> CLASSIC_KATANA = REGISTRY.register("classic_katana", () -> new ClassicKatanaItem());
	public static final RegistryObject<Item> BLOOD_KATANA = REGISTRY.register("blood_katana", () -> new BloodKatanaItem());
	public static final RegistryObject<Item> MOONS_LUNAR_BLOOMFANG = REGISTRY.register("moons_lunar_bloomfang", () -> new MoonsLunarBloomfangItem());
	public static final RegistryObject<Item> HOLLOW_MAN_RUYI_JINGU_STAFF = REGISTRY.register("hollow_man_ruyi_jingu_staff", () -> new HollowManRuyiJinguStaffItem());
	public static final RegistryObject<Item> WEAPONTEMPLATE = REGISTRY.register("weapontemplate", () -> new WeapontemplateItem());
	public static final RegistryObject<Item> CLASSICHAMMER = REGISTRY.register("classichammer", () -> new ClassichammerItem());
	public static final RegistryObject<Item> EARTHLYHAMMER = REGISTRY.register("earthlyhammer", () -> new EarthlyhammerItem());
	public static final RegistryObject<Item> OBSIDIANHAMMER = REGISTRY.register("obsidianhammer", () -> new ObsidianhammerItem());
	public static final RegistryObject<Item> ROSEGOLDHAMMER = REGISTRY.register("rosegoldhammer", () -> new RosegoldhammerItem());
	public static final RegistryObject<Item> EARTHLY_INGOT = REGISTRY.register("earthly_ingot", () -> new EarthlyIngotItem());
	public static final RegistryObject<Item> OBSIDIAN_FORGED_INGOT = REGISTRY.register("obsidian_forged_ingot", () -> new ObsidianForgedIngotItem());
	public static final RegistryObject<Item> ROSE_GOLD_INGOT = REGISTRY.register("rose_gold_ingot", () -> new RoseGoldIngotItem());
	public static final RegistryObject<Item> HELISH_SWORD = REGISTRY.register("helish_sword", () -> new HelishSwordItem());
	public static final RegistryObject<Item> IMMORTAL_SCYTHE = REGISTRY.register("immortal_scythe", () -> new ImmortalScytheItem());
	public static final RegistryObject<Item> INOSUKESSWORD = REGISTRY.register("inosukessword", () -> new InosukesswordItem());
	public static final RegistryObject<Item> WARAXE = REGISTRY.register("waraxe", () -> new WaraxeItem());
	public static final RegistryObject<Item> ZENITSUS_SWORD = REGISTRY.register("zenitsus_sword", () -> new ZenitsusSwordItem());
	public static final RegistryObject<Item> BONE_SWORD = REGISTRY.register("bone_sword", () -> new BoneSwordItem());
	public static final RegistryObject<Item> WEAPONS_FORGE_ACTIVE = block(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE);
	public static final RegistryObject<Item> WEAPONS_FORGE_UN_ACTIVE = block(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE);
	public static final RegistryObject<Item> RESEARCH_TABLE = block(FineTunedWeaponryModBlocks.RESEARCH_TABLE);
	public static final RegistryObject<Item> GEM_1 = REGISTRY.register("gem_1", () -> new Gem1Item());

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
