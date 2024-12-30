
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import com.naizo.finetuned.item.WeapontemplateItem;
import com.naizo.finetuned.item.RosegoldhammerItem;
import com.naizo.finetuned.item.RoseGoldIngotItem;
import com.naizo.finetuned.item.ObsidianhammerItem;
import com.naizo.finetuned.item.ObsidianForgedIngotItem;
import com.naizo.finetuned.item.MoonsLunarBloomfangItem;
import com.naizo.finetuned.item.KurasaiKatanaItem;
import com.naizo.finetuned.item.HollowManRuyiJinguStaffItem;
import com.naizo.finetuned.item.EarthlyhammerItem;
import com.naizo.finetuned.item.EarthlyIngotItem;
import com.naizo.finetuned.item.ClassichammerItem;
import com.naizo.finetuned.item.ClassicKatanaItem;
import com.naizo.finetuned.item.BloodKatanaItem;
import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<Item> CLASSIC_KATANA = REGISTRY.register("classic_katana", () -> new ClassicKatanaItem());
	public static final RegistryObject<Item> BLOOD_KATANA = REGISTRY.register("blood_katana", () -> new BloodKatanaItem());
	public static final RegistryObject<Item> MOONS_LUNAR_BLOOMFANG = REGISTRY.register("moons_lunar_bloomfang", () -> new MoonsLunarBloomfangItem());
	public static final RegistryObject<Item> KURASAI_KATANA = REGISTRY.register("kurasai_katana", () -> new KurasaiKatanaItem());
	public static final RegistryObject<Item> HOLLOW_MAN_RUYI_JINGU_STAFF = REGISTRY.register("hollow_man_ruyi_jingu_staff", () -> new HollowManRuyiJinguStaffItem());
	public static final RegistryObject<Item> WEAPONTEMPLATE = REGISTRY.register("weapontemplate", () -> new WeapontemplateItem());
	public static final RegistryObject<Item> CLASSICHAMMER = REGISTRY.register("classichammer", () -> new ClassichammerItem());
	public static final RegistryObject<Item> EARTHLYHAMMER = REGISTRY.register("earthlyhammer", () -> new EarthlyhammerItem());
	public static final RegistryObject<Item> OBSIDIANHAMMER = REGISTRY.register("obsidianhammer", () -> new ObsidianhammerItem());
	public static final RegistryObject<Item> ROSEGOLDHAMMER = REGISTRY.register("rosegoldhammer", () -> new RosegoldhammerItem());
	public static final RegistryObject<Item> EARTHLY_INGOT = REGISTRY.register("earthly_ingot", () -> new EarthlyIngotItem());
	public static final RegistryObject<Item> OBSIDIAN_FORGED_INGOT = REGISTRY.register("obsidian_forged_ingot", () -> new ObsidianForgedIngotItem());
	public static final RegistryObject<Item> ROSE_GOLD_INGOT = REGISTRY.register("rose_gold_ingot", () -> new RoseGoldIngotItem());
	// Start of user code block custom items
	// End of user code block custom items
}
