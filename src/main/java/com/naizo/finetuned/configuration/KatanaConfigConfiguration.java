package com.naizo.finetuned.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class KatanaConfigConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Double> CLASSIC_KATANA_COOLDOWN;
	public static final ForgeConfigSpec.ConfigValue<Double> CLASSIC_KATANA_DASH_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> BLOOD_KATANA_COOLDOWN;
	public static final ForgeConfigSpec.ConfigValue<Double> BLOOD_KATANA_DASH_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> BLOOD_KATANA_LIFESTEAL;
	public static final ForgeConfigSpec.ConfigValue<Double> KURASAI_KATANA_LIGHTNING_STRIKES;
	public static final ForgeConfigSpec.ConfigValue<Double> KURASAI_KATANA_RANGE;
	public static final ForgeConfigSpec.ConfigValue<Double> KURASAI_KATANA_BUFF_POWER;
	public static final ForgeConfigSpec.ConfigValue<Double> KURASAI_KATANA_BUFF_DURATION;
	public static final ForgeConfigSpec.ConfigValue<Double> KURASAI_KATANA_BUFF_CDR;
	static {
		BUILDER.push("Classic Katana");
		CLASSIC_KATANA_COOLDOWN = BUILDER.define("dash_cooldown", (double) 40);
		CLASSIC_KATANA_DASH_POWER = BUILDER.define("dash_power", (double) 2);
		BUILDER.pop();
		BUILDER.push("Blood Katana");
		BLOOD_KATANA_COOLDOWN = BUILDER.define("dash_cooldown", (double) 40);
		BLOOD_KATANA_DASH_POWER = BUILDER.define("dash_power", (double) 2);
		BLOOD_KATANA_LIFESTEAL = BUILDER.define("life-steal", (double) 4);
		BUILDER.pop();
		BUILDER.push("Kurasai Katana");
		KURASAI_KATANA_LIGHTNING_STRIKES = BUILDER.define("kurasai_katana_lightning_strikes", (double) 2);
		KURASAI_KATANA_RANGE = BUILDER.define("kurasai_katana_range", (double) 20);
		KURASAI_KATANA_BUFF_POWER = BUILDER.define("buff_power", (double) 4);
		KURASAI_KATANA_BUFF_DURATION = BUILDER.define("buff_duration", (double) 20);
		KURASAI_KATANA_BUFF_CDR = BUILDER.define("buff_cdr", (double) 150);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
