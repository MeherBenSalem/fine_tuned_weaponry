package com.naizo.finetuned.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class HammerConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Double> EARTH_HAMMER_CDR;
	public static final ForgeConfigSpec.ConfigValue<Double> EARTH_HAMMER_RANGE;
	public static final ForgeConfigSpec.ConfigValue<Double> EARTH_HAMMER_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Double> OB_HAMMER_CDR;
	public static final ForgeConfigSpec.ConfigValue<Double> OB_HAMMER_RANGE;
	public static final ForgeConfigSpec.ConfigValue<Double> OB_HAMMER_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Double> RG_HAMMER_CDR;
	public static final ForgeConfigSpec.ConfigValue<Double> RG_HAMMER_RANGE;
	public static final ForgeConfigSpec.ConfigValue<Double> RG_HAMMER_BUFF_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> RG_HAMMER_BUFF_DURATION;
	static {
		BUILDER.push("Earthly Hammer");
		EARTH_HAMMER_CDR = BUILDER.define("cooldown", (double) 100);
		EARTH_HAMMER_RANGE = BUILDER.define("range", (double) 10);
		EARTH_HAMMER_DAMAGE = BUILDER.define("damage", (double) 10);
		BUILDER.pop();
		BUILDER.push("Obsidian Hammer");
		OB_HAMMER_CDR = BUILDER.define("cooldown", (double) 300);
		OB_HAMMER_RANGE = BUILDER.define("range", (double) 10);
		OB_HAMMER_DAMAGE = BUILDER.define("damage", (double) 30);
		BUILDER.pop();
		BUILDER.push("Rose Gold Hammer");
		RG_HAMMER_CDR = BUILDER.define("cooldown", (double) 300);
		RG_HAMMER_RANGE = BUILDER.define("range", (double) 10);
		RG_HAMMER_BUFF_POWER = BUILDER.define("buff_power", (double) 1);
		RG_HAMMER_BUFF_DURATION = BUILDER.define("buff_duration", (double) 100);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
