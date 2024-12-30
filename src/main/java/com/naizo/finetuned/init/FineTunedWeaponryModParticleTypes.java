
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.naizo.finetuned.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import com.naizo.finetuned.FineTunedWeaponryMod;

public class FineTunedWeaponryModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, FineTunedWeaponryMod.MODID);
	public static final RegistryObject<SimpleParticleType> ROSE_PARTICLE = REGISTRY.register("rose_particle", () -> new SimpleParticleType(true));
}
