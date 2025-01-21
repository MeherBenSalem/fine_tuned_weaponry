package com.naizo.finetuned.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class KatanaConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Double> CLASSIC_KATANA_COOLDOWN;
	public static final ForgeConfigSpec.ConfigValue<Double> CLASSIC_KATANA_DASH_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> BLOOD_KATANA_COOLDOWN;
	public static final ForgeConfigSpec.ConfigValue<Double> BLOOD_KATANA_DASH_POWER;
	static {
		BUILDER.push("Classic Katana");
		CLASSIC_KATANA_COOLDOWN = BUILDER.define("dash_cooldown", (double) 40);
		CLASSIC_KATANA_DASH_POWER = BUILDER.define("dash_power", (double) 2);
		BUILDER.pop();
		BUILDER.push("Blood Katana");
		BLOOD_KATANA_COOLDOWN = BUILDER.define("dash_cooldown", (double) 40);
		BLOOD_KATANA_DASH_POWER = BUILDER.define("dash_power", (double) 2);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
