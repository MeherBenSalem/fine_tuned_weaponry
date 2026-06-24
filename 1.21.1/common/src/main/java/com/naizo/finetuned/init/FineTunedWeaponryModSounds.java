package com.naizo.finetuned.init;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.registry.RegistryHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class FineTunedWeaponryModSounds {
	public static final RegistryHolder<SoundEvent> ACTIVATION_SOUND_EFFECT =
			new RegistryHolder<>("activation_sound_effect", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "activation_sound_effect")));
	public static final RegistryHolder<SoundEvent> KATANA_DASH_SOUND =
			new RegistryHolder<>("katana_dash_sound", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "katana_dash_sound")));
	public static final RegistryHolder<SoundEvent> LOVE_EFFECT_SOUND =
			new RegistryHolder<>("love_effect_sound", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "love_effect_sound")));

	private FineTunedWeaponryModSounds() {
	}
}
