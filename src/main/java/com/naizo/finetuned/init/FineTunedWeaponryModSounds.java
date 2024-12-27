
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<SoundEvent> ACTIVATION_SOUND_EFFECT = REGISTRY.register("activation_sound_effect", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("fine_tuned_weaponry", "activation_sound_effect")));
	public static final RegistryObject<SoundEvent> KATANA_DASH_SOUND = REGISTRY.register("katana_dash_sound", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("fine_tuned_weaponry", "katana_dash_sound")));
	public static final RegistryObject<SoundEvent> LOVE_EFFECT_SOUND = REGISTRY.register("love_effect_sound", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("fine_tuned_weaponry", "love_effect_sound")));
}
