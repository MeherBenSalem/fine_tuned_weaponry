package com.naizo.finetuned.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class FangConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Double> MOON_FANG_CDR;
	public static final ForgeConfigSpec.ConfigValue<Double> MOON_FANG_RANGE;
	static {
		BUILDER.push("Moon's Lunar Bloomfang");
		MOON_FANG_CDR = BUILDER.define("cooldown", (double) 300);
		MOON_FANG_RANGE = BUILDER.define("range", (double) 10);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
