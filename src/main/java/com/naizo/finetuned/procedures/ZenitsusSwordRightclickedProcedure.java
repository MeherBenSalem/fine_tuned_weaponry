package com.naizo.finetuned.procedures;

import tn.naizo.jauml.JaumlConfigLib;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class ZenitsusSwordRightclickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_strike_range") / 2d), e -> true).stream()
					.sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (!(entityiterator == entity) && entityiterator instanceof LivingEntity) {
					for (int index0 = 0; index0 < (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_strike_number"); index0++) {
						if (world instanceof ServerLevel _level) {
							LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(_level);
							entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ())));;
							_level.addFreshEntity(entityToSpawn);
						}
					}
				}
			}
		}
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_speed_duration"),
					(int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_speed_level")));
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_speed_duration"),
					(int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_speed_level")));
		if (entity instanceof Player _player)
			_player.getCooldowns().addCooldown(itemstack.getItem(), (int) JaumlConfigLib.getNumberValue("finetunned/weapons", "swords_config", "zenitsus_cooldown"));
	}
}
