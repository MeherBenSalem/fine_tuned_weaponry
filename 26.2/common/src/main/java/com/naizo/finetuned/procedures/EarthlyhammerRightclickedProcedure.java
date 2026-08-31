package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;
import tn.naizo.jauml.JaumlConfigLib;

import java.util.List;

public class EarthlyhammerRightclickedProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null || !(entity instanceof LivingEntity attacker)) {
			return;
		}
		if (entity instanceof Player player && player.getCooldowns().isOnCooldown(itemstack.getItem())) {
			return;
		}
		double range = JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "earth_hammer_aoe_range");
		float damage = (float) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "earth_hammer_aoe_damage");
		double knockback = JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "earth_hammer_knockback");
		Vec3 center = entity.position();
		AABB box = new AABB(center, center).inflate(range);
		List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box, e -> e != entity && e.isAttackable());
		DamageSource source = new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), attacker);
		for (LivingEntity target : targets) {
			target.hurt(source, damage);
			Vec3 push = target.position().subtract(center).normalize().scale(knockback);
			target.push(push.x, 0.35, push.z);
			world.addParticle(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 0, 0, 0);
		}
		if (entity instanceof Player player) {
			player.getCooldowns().addCooldown(itemstack.getItem(), (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "earth_hammer_cooldown"));
		}
	}
}
