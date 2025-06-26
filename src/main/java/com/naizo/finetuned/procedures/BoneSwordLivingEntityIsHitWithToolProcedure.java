package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class BoneSwordLivingEntityIsHitWithToolProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "bone_sword_effect_duration"),
					(int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "bone_sword_effect_level"), false, false));
	}
}
