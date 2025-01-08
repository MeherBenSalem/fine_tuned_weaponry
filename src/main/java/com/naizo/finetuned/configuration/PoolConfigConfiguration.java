package com.naizo.finetuned.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class PoolConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Double> HOLLOW_HASTE_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> HOLLOW_HASTE_DURATION;
	public static final ForgeConfigSpec.ConfigValue<Double> HOLLOW_HASTE_CDR;
	static {
		BUILDER.push("Hollow Man's Ruyi Jingu Staff");
		HOLLOW_HASTE_POWER = BUILDER.define("Buff_Level", (double) 2);
		HOLLOW_HASTE_DURATION = BUILDER.define("Buff_Duration", (double) 7);
		HOLLOW_HASTE_CDR = BUILDER.comment("in ticks (1 Tick = 0.05 sec)").define("Cooldown", (double) 250);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
