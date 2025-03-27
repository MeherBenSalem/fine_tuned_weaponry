
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
import com.naizo.finetuned.item.WildfireAmplifierItem;
import com.naizo.finetuned.item.WeapontemplateItem;
import com.naizo.finetuned.item.WaraxeItem;
import com.naizo.finetuned.item.VolcanicBurstAmplifierItem;
import com.naizo.finetuned.item.SuperstormAmplifierItem;
import com.naizo.finetuned.item.StormShardItem;
import com.naizo.finetuned.item.StormFuryAmplifierItem;
import com.naizo.finetuned.item.StaticAmplifierItem;
import com.naizo.finetuned.item.SmokescreenAmplifierItem;
import com.naizo.finetuned.item.RosegoldhammerItem;
import com.naizo.finetuned.item.RoseGoldIngotItem;
import com.naizo.finetuned.item.PyroclasmAmplifierItem;
import com.naizo.finetuned.item.PermafrostAmplifierItem;
import com.naizo.finetuned.item.OverloadAmplifierItem;
import com.naizo.finetuned.item.ObsidianhammerItem;
import com.naizo.finetuned.item.ObsidianForgedIngotItem;
import com.naizo.finetuned.item.MoonsLunarBloomfangItem;
import com.naizo.finetuned.item.LavaInfusionAmplifierItem;
import com.naizo.finetuned.item.InosukesswordItem;
import com.naizo.finetuned.item.InfernoCoreItem;
import com.naizo.finetuned.item.InfernalHungerAmplifierItem;
import com.naizo.finetuned.item.ImmortalScytheItem;
import com.naizo.finetuned.item.HollowManRuyiJinguStaffItem;
import com.naizo.finetuned.item.HellfireAmplifierItem;
import com.naizo.finetuned.item.HelishSwordItem;
import com.naizo.finetuned.item.GlacialAmplifierItem;
import com.naizo.finetuned.item.FrostRuneItem;
import com.naizo.finetuned.item.FinetunnedGuideItem;
import com.naizo.finetuned.item.EarthlyhammerItem;
import com.naizo.finetuned.item.EarthlyIngotItem;
import com.naizo.finetuned.item.ClassichammerItem;
import com.naizo.finetuned.item.ClassicKatanaItem;
import com.naizo.finetuned.item.ChargedAmplifierItem;
import com.naizo.finetuned.item.BoneSwordItem;
import com.naizo.finetuned.item.BloodKatanaItem;
import com.naizo.finetuned.item.BlizzardAmplifierItem;
import com.naizo.finetuned.item.BlazingAmplifierItem;
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
	public static final RegistryObject<Item> INFERNO_CORE = REGISTRY.register("inferno_core", () -> new InfernoCoreItem());
	public static final RegistryObject<Item> STORM_SHARD = REGISTRY.register("storm_shard", () -> new StormShardItem());
	public static final RegistryObject<Item> FROST_RUNE = REGISTRY.register("frost_rune", () -> new FrostRuneItem());
	public static final RegistryObject<Item> BLAZING_AMPLIFIER = REGISTRY.register("blazing_amplifier", () -> new BlazingAmplifierItem());
	public static final RegistryObject<Item> WILDFIRE_AMPLIFIER = REGISTRY.register("wildfire_amplifier", () -> new WildfireAmplifierItem());
	public static final RegistryObject<Item> HELLFIRE_AMPLIFIER = REGISTRY.register("hellfire_amplifier", () -> new HellfireAmplifierItem());
	public static final RegistryObject<Item> CHARGED_AMPLIFIER = REGISTRY.register("charged_amplifier", () -> new ChargedAmplifierItem());
	public static final RegistryObject<Item> OVERLOAD_AMPLIFIER = REGISTRY.register("overload_amplifier", () -> new OverloadAmplifierItem());
	public static final RegistryObject<Item> SUPERSTORM_AMPLIFIER = REGISTRY.register("superstorm_amplifier", () -> new SuperstormAmplifierItem());
	public static final RegistryObject<Item> GLACIAL_AMPLIFIER = REGISTRY.register("glacial_amplifier", () -> new GlacialAmplifierItem());
	public static final RegistryObject<Item> PERMAFROST_AMPLIFIER = REGISTRY.register("permafrost_amplifier", () -> new PermafrostAmplifierItem());
	public static final RegistryObject<Item> BLIZZARD_AMPLIFIER = REGISTRY.register("blizzard_amplifier", () -> new BlizzardAmplifierItem());
	public static final RegistryObject<Item> PYROCLASM_AMPLIFIER = REGISTRY.register("pyroclasm_amplifier", () -> new PyroclasmAmplifierItem());
	public static final RegistryObject<Item> INFERNAL_HUNGER_AMPLIFIER = REGISTRY.register("infernal_hunger_amplifier", () -> new InfernalHungerAmplifierItem());
	public static final RegistryObject<Item> LAVA_INFUSION_AMPLIFIER = REGISTRY.register("lava_infusion_amplifier", () -> new LavaInfusionAmplifierItem());
	public static final RegistryObject<Item> SMOKESCREEN_AMPLIFIER = REGISTRY.register("smokescreen_amplifier", () -> new SmokescreenAmplifierItem());
	public static final RegistryObject<Item> VOLCANIC_BURST_AMPLIFIER = REGISTRY.register("volcanic_burst_amplifier", () -> new VolcanicBurstAmplifierItem());
	public static final RegistryObject<Item> STORM_FURY_AMPLIFIER = REGISTRY.register("storm_fury_amplifier", () -> new StormFuryAmplifierItem());
	public static final RegistryObject<Item> STATIC_AMPLIFIER = REGISTRY.register("static_amplifier", () -> new StaticAmplifierItem());
	public static final RegistryObject<Item> WEAPONS_ANVIL = block(FineTunedWeaponryModBlocks.WEAPONS_ANVIL);
	public static final RegistryObject<Item> FINETUNNED_GUIDE = REGISTRY.register("finetunned_guide", () -> new FinetunnedGuideItem());

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
