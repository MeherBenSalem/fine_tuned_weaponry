package com.naizo.finetuned.procedures;

import com.naizo.finetuned.util.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tn.naizo.jauml.JaumlConfigLib;

import java.util.List;

public class ObsidianhammerRightclickedProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null || !(world instanceof Level level)) {
			return;
		}
		if (entity instanceof Player player && player.getCooldowns().isOnCooldown(itemstack.getItem())) {
			return;
		}
		Vec3 look = entity.getLookAngle();
		double strikeX = entity.getX() + look.x * 4;
		double strikeY = entity.getY();
		double strikeZ = entity.getZ() + look.z * 4;
		float power = (float) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "obsidian_hammer_explosion_power");
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.explode(entity, strikeX, strikeY, strikeZ, power, Level.ExplosionInteraction.MOB);
		}
		double range = JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "obsidian_hammer_fire_range");
		Vec3 center = new Vec3(strikeX, strikeY, strikeZ);
		List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(range), e -> e != entity && e.isAttackable());
		int fireSeconds = (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "obsidian_hammer_fire_seconds");
		for (LivingEntity target : targets) {
			target.igniteForSeconds(fireSeconds);
		}
		for (int i = 0; i < 24; i++) {
			world.addParticle(ParticleTypes.LAVA, strikeX, strikeY + 1, strikeZ, (level.random.nextDouble() - 0.5) * 0.4, 0.2, (level.random.nextDouble() - 0.5) * 0.4);
		}
		if (entity instanceof Player player) {
			player.getCooldowns().addCooldown(itemstack.getItem(), (int) JaumlConfigLib.getNumberValue(ModConfig.WEAPONS, "hammer_config", "obsidian_hammer_cooldown"));
		}
	}
}
