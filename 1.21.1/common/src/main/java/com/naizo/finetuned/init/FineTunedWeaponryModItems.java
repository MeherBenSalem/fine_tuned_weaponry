package com.naizo.finetuned.init;

import com.naizo.finetuned.item.*;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class FineTunedWeaponryModItems {
	public static final RegistryHolder<Item> CLASSIC_KATANA = new RegistryHolder<>("classic_katana", ClassicKatanaItem::new);
	public static final RegistryHolder<Item> BLOOD_KATANA = new RegistryHolder<>("blood_katana", BloodKatanaItem::new);
	public static final RegistryHolder<Item> MOONS_LUNAR_BLOOMFANG = new RegistryHolder<>("moons_lunar_bloomfang", MoonsLunarBloomfangItem::new);
	public static final RegistryHolder<Item> HOLLOW_MAN_RUYI_JINGU_STAFF = new RegistryHolder<>("hollow_man_ruyi_jingu_staff", HollowManRuyiJinguStaffItem::new);
	public static final RegistryHolder<Item> WEAPONTEMPLATE = new RegistryHolder<>("weapontemplate", WeapontemplateItem::new);
	public static final RegistryHolder<Item> CLASSICHAMMER = new RegistryHolder<>("classichammer", ClassichammerItem::new);
	public static final RegistryHolder<Item> EARTHLYHAMMER = new RegistryHolder<>("earthlyhammer", EarthlyhammerItem::new);
	public static final RegistryHolder<Item> OBSIDIANHAMMER = new RegistryHolder<>("obsidianhammer", ObsidianhammerItem::new);
	public static final RegistryHolder<Item> ROSEGOLDHAMMER = new RegistryHolder<>("rosegoldhammer", RosegoldhammerItem::new);
	public static final RegistryHolder<Item> EARTHLY_INGOT = new RegistryHolder<>("earthly_ingot", EarthlyIngotItem::new);
	public static final RegistryHolder<Item> OBSIDIAN_FORGED_INGOT = new RegistryHolder<>("obsidian_forged_ingot", ObsidianForgedIngotItem::new);
	public static final RegistryHolder<Item> ROSE_GOLD_INGOT = new RegistryHolder<>("rose_gold_ingot", RoseGoldIngotItem::new);
	public static final RegistryHolder<Item> HELISH_SWORD = new RegistryHolder<>("helish_sword", HelishSwordItem::new);
	public static final RegistryHolder<Item> IMMORTAL_SCYTHE = new RegistryHolder<>("immortal_scythe", ImmortalScytheItem::new);
	public static final RegistryHolder<Item> INOSUKESSWORD = new RegistryHolder<>("inosukessword", InosukesswordItem::new);
	public static final RegistryHolder<Item> WARAXE = new RegistryHolder<>("waraxe", WaraxeItem::new);
	public static final RegistryHolder<Item> ZENITSUS_SWORD = new RegistryHolder<>("zenitsus_sword", ZenitsusSwordItem::new);
	public static final RegistryHolder<Item> BONE_SWORD = new RegistryHolder<>("bone_sword", BoneSwordItem::new);
	public static final RegistryHolder<Item> WEAPONS_FORGE_ACTIVE = blockItem(FineTunedWeaponryModBlocks.WEAPONS_FORGE_ACTIVE);
	public static final RegistryHolder<Item> WEAPONS_FORGE_UN_ACTIVE = blockItem(FineTunedWeaponryModBlocks.WEAPONS_FORGE_UN_ACTIVE);
	public static final RegistryHolder<Item> RESEARCH_TABLE = blockItem(FineTunedWeaponryModBlocks.RESEARCH_TABLE);
	public static final RegistryHolder<Item> INFERNO_CORE = new RegistryHolder<>("inferno_core", InfernoCoreItem::new);
	public static final RegistryHolder<Item> STORM_SHARD = new RegistryHolder<>("storm_shard", StormShardItem::new);
	public static final RegistryHolder<Item> FROST_RUNE = new RegistryHolder<>("frost_rune", FrostRuneItem::new);
	public static final RegistryHolder<Item> BLAZING_AMPLIFIER = new RegistryHolder<>("blazing_amplifier", BlazingAmplifierItem::new);
	public static final RegistryHolder<Item> WILDFIRE_AMPLIFIER = new RegistryHolder<>("wildfire_amplifier", WildfireAmplifierItem::new);
	public static final RegistryHolder<Item> HELLFIRE_AMPLIFIER = new RegistryHolder<>("hellfire_amplifier", HellfireAmplifierItem::new);
	public static final RegistryHolder<Item> CHARGED_AMPLIFIER = new RegistryHolder<>("charged_amplifier", ChargedAmplifierItem::new);
	public static final RegistryHolder<Item> OVERLOAD_AMPLIFIER = new RegistryHolder<>("overload_amplifier", OverloadAmplifierItem::new);
	public static final RegistryHolder<Item> SUPERSTORM_AMPLIFIER = new RegistryHolder<>("superstorm_amplifier", SuperstormAmplifierItem::new);
	public static final RegistryHolder<Item> GLACIAL_AMPLIFIER = new RegistryHolder<>("glacial_amplifier", GlacialAmplifierItem::new);
	public static final RegistryHolder<Item> PERMAFROST_AMPLIFIER = new RegistryHolder<>("permafrost_amplifier", PermafrostAmplifierItem::new);
	public static final RegistryHolder<Item> BLIZZARD_AMPLIFIER = new RegistryHolder<>("blizzard_amplifier", BlizzardAmplifierItem::new);
	public static final RegistryHolder<Item> PYROCLASM_AMPLIFIER = new RegistryHolder<>("pyroclasm_amplifier", PyroclasmAmplifierItem::new);
	public static final RegistryHolder<Item> INFERNAL_HUNGER_AMPLIFIER = new RegistryHolder<>("infernal_hunger_amplifier", InfernalHungerAmplifierItem::new);
	public static final RegistryHolder<Item> LAVA_INFUSION_AMPLIFIER = new RegistryHolder<>("lava_infusion_amplifier", LavaInfusionAmplifierItem::new);
	public static final RegistryHolder<Item> SMOKESCREEN_AMPLIFIER = new RegistryHolder<>("smokescreen_amplifier", SmokescreenAmplifierItem::new);
	public static final RegistryHolder<Item> VOLCANIC_BURST_AMPLIFIER = new RegistryHolder<>("volcanic_burst_amplifier", VolcanicBurstAmplifierItem::new);
	public static final RegistryHolder<Item> STORM_FURY_AMPLIFIER = new RegistryHolder<>("storm_fury_amplifier", StormFuryAmplifierItem::new);
	public static final RegistryHolder<Item> STATIC_AMPLIFIER = new RegistryHolder<>("static_amplifier", StaticAmplifierItem::new);
	public static final RegistryHolder<Item> WEAPONS_ANVIL = blockItem(FineTunedWeaponryModBlocks.WEAPONS_ANVIL);

	private static RegistryHolder<Item> blockItem(RegistryHolder<Block> block) {
		return new RegistryHolder<>(block.path(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private FineTunedWeaponryModItems() {
	}
}
