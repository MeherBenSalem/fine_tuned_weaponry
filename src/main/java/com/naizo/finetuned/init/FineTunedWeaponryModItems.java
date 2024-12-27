
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import com.naizo.finetuned.item.WeapontemplateItem;
import com.naizo.finetuned.item.MoonsLunarBloomfangItem;
import com.naizo.finetuned.item.KurasaiKatanaItem;
import com.naizo.finetuned.item.HollowManRuyiJinguStaffItem;
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
	// Start of user code block custom items
	// End of user code block custom items
}
