package io.nightbeam.runeborn.compat;

import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.weapon.WeaponClass;
import io.nightbeam.runeborn.mc.Fx;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Minecraft 26.3 implementation of every call that differs between supported versions.
 * The same class, with the same signatures, exists in each version workspace.
 */
public final class Compat {
    private static final String STACK_ROOT = Rb.MOD_ID;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Rb.MOD_ID, path);
    }

    // ------------------------------------------------------------------ ids and world

    public static String entityTypeId(Entity e) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
    }

    public static String dimensionId(Level level) {
        return level.dimension().identifier().toString();
    }

    public static String biomeId(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey().map(k -> k.identifier().toString()).orElse("minecraft:plains");
    }

    public static ServerLevel level(MinecraftServer server, String dimension) {
        Identifier loc = Identifier.tryParse(dimension);
        return loc == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, loc));
    }

    public static BlockPos spawnPos(ServerLevel level) {
        return level.getRespawnData().pos();
    }

    public static int surface(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    public static boolean isNight(ServerLevel level) {
        return level.isDarkOutside();
    }

    // ------------------------------------------------------------------ entities

    public static Set<String> tags(Entity e) {
        return e.entityTags();
    }

    public static Entity create(ServerLevel level, String typeId) {
        Identifier loc = Identifier.tryParse(typeId);
        if (loc == null) {
            return null;
        }
        return BuiltInRegistries.ENTITY_TYPE.getOptional(loc).map(t -> (Entity) t.create(level, EntitySpawnReason.MOB_SUMMONED)).orElse(null);
    }

    public static void push(Entity e, Vec3 velocity) {
        e.setDeltaMovement(velocity);
        e.needsSync = true;
    }

    /** Turns {@code e} to look at {@code target}; players are re-synced to their client. */
    public static void face(Entity e, Entity target) {
        double dx = target.getX() - e.getX();
        double dz = target.getZ() - e.getZ();
        double dy = target.getEyeY() - e.getEyeY();
        float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90f;
        float pitch = (float) -(Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * Mth.RAD_TO_DEG);
        if (e instanceof ServerPlayer p) {
            p.connection.teleport(p.getX(), p.getY(), p.getZ(), yaw, pitch);
        } else {
            e.setYRot(yaw);
            e.setXRot(pitch);
            e.setYHeadRot(yaw);
        }
    }

    public static void ride(Entity rider, Entity mount) {
        rider.startRiding(mount, true, false);
    }

    public static boolean pvpAllowed(LivingEntity attacker) {
        return attacker.level() instanceof ServerLevel level && level.getGameRules().get(GameRules.PVP);
    }

    public static boolean isIgnoredTarget(Entity e) {
        return e instanceof ArmorStand || e.isSpectator();
    }

    public static void equip(Mob mob, ItemStack stack) {
        mob.setItemSlot(EquipmentSlot.MAINHAND, stack);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0f);
    }

    public static void prepareBoss(Mob mob) {
        if (mob instanceof AbstractPiglin piglin) {
            piglin.setImmuneToZombification(true);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mob.setDropChance(slot, 0f);
        }
    }

    // ------------------------------------------------------------------ damage

    public static boolean hurt(LivingEntity target, DamageSource source, float amount) {
        return target.level() instanceof ServerLevel level && target.hurtServer(level, source, amount);
    }

    public static DamageSource attackSource(LivingEntity attacker) {
        if (attacker instanceof Player p) {
            return p.damageSources().playerAttack(p);
        }
        return attacker.damageSources().mobAttack(attacker);
    }

    public static DamageSource magicSource(LivingEntity attacker) {
        return attacker.damageSources().indirectMagic(attacker, attacker);
    }

    public static DamageSource genericMagic(Entity target) {
        return target.damageSources().magic();
    }

    public static boolean isFire(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE);
    }

    public static boolean isMagic(DamageSource source) {
        return source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC);
    }

    public static boolean bypassesInvulnerability(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public static boolean isUndead(LivingEntity e) {
        return e.is(EntityTypeTags.UNDEAD);
    }

    public static void ignite(Entity e, int seconds) {
        e.igniteForSeconds(seconds);
    }

    public static void lightning(ServerLevel level, Vec3 pos, boolean visualOnly) {
        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.snapTo(pos.x, pos.y, pos.z);
            bolt.setVisualOnly(visualOnly);
            level.addFreshEntity(bolt);
        }
    }

    /** Chunks of the ground block flying up, for slams. */
    public static void blockBurst(ServerLevel level, Vec3 at, double radius, int count) {
        BlockPos below = BlockPos.containing(at.x, at.y - 0.5, at.z);
        var state = level.getBlockState(below);
        if (state.isAir()) {
            return;
        }
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), at.x, at.y + 0.1, at.z, count,
                radius * 0.4, 0.1, radius * 0.4, 0.15);
    }

    // ------------------------------------------------------------------ effects

    private static Holder<MobEffect> effect(Fx.E e) {
        return switch (e) {
            case SLOWNESS -> MobEffects.SLOWNESS;
            case POISON -> MobEffects.POISON;
            case WITHER -> MobEffects.WITHER;
            case REGENERATION -> MobEffects.REGENERATION;
            case RESISTANCE -> MobEffects.RESISTANCE;
            case STRENGTH -> MobEffects.STRENGTH;
            case SPEED -> MobEffects.SPEED;
            case WEAKNESS -> MobEffects.WEAKNESS;
            case BLINDNESS -> MobEffects.BLINDNESS;
            case DARKNESS -> MobEffects.DARKNESS;
            case FIRE_RESISTANCE -> MobEffects.FIRE_RESISTANCE;
            case ABSORPTION -> MobEffects.ABSORPTION;
            case GLOWING -> MobEffects.GLOWING;
            case LEVITATION -> MobEffects.LEVITATION;
            case SLOW_FALLING -> MobEffects.SLOW_FALLING;
            case NAUSEA -> MobEffects.NAUSEA;
            case HASTE -> MobEffects.HASTE;
            case MINING_FATIGUE -> MobEffects.MINING_FATIGUE;
        };
    }

    /** {@code ticks < 0} means infinite. */
    public static void addEffect(LivingEntity e, Fx.E effect, int ticks, int amplifier) {
        int duration = ticks < 0 ? MobEffectInstance.INFINITE_DURATION : ticks;
        e.addEffect(new MobEffectInstance(effect(effect), duration, amplifier, false, ticks >= 0));
    }

    public static void clearHarmful(LivingEntity e) {
        List<Holder<MobEffect>> remove = new ArrayList<>();
        for (MobEffectInstance inst : e.getActiveEffects()) {
            if (inst.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                remove.add(inst.getEffect());
            }
        }
        remove.forEach(e::removeEffect);
        e.clearFire();
    }

    // ------------------------------------------------------------------ attributes

    private static Holder<Attribute> attribute(Fx.A a) {
        return switch (a) {
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case KNOCKBACK_RESISTANCE -> Attributes.KNOCKBACK_RESISTANCE;
            case LUCK -> Attributes.LUCK;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case ARMOR -> Attributes.ARMOR;
            case SCALE -> Attributes.SCALE;
            case ENTITY_REACH -> Attributes.ENTITY_INTERACTION_RANGE;
            case FOLLOW_RANGE -> Attributes.FOLLOW_RANGE;
        };
    }

    /** Sets (replacing) a permanent modifier. {@code multiplyBase} adds {@code amount * base}. */
    public static void setModifier(LivingEntity e, Fx.A a, String id, double amount, boolean multiplyBase) {
        AttributeInstance inst = e.getAttribute(attribute(a));
        if (inst == null) {
            return;
        }
        Identifier loc = id(id);
        inst.removeModifier(loc);
        if (amount != 0) {
            inst.addPermanentModifier(new AttributeModifier(loc, amount,
                    multiplyBase ? AttributeModifier.Operation.ADD_MULTIPLIED_BASE : AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void setBase(LivingEntity e, Fx.A a, double value) {
        AttributeInstance inst = e.getAttribute(attribute(a));
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    public static double attrBase(LivingEntity e, Fx.A a) {
        AttributeInstance inst = e.getAttribute(attribute(a));
        return inst == null ? 0 : inst.getBaseValue();
    }

    public static double attrValue(LivingEntity e, Fx.A a) {
        AttributeInstance inst = e.getAttribute(attribute(a));
        return inst == null ? 0 : inst.getValue();
    }

    public static double luck(Player p) {
        return p.getLuck();
    }

    // ------------------------------------------------------------------ particles and sounds

    private static ParticleOptions particle(Fx.P p) {
        return switch (p) {
            case FLAME -> ParticleTypes.FLAME;
            case SOUL_FIRE -> ParticleTypes.SOUL_FIRE_FLAME;
            case SMOKE -> ParticleTypes.SMOKE;
            case LARGE_SMOKE -> ParticleTypes.LARGE_SMOKE;
            case CLOUD -> ParticleTypes.CLOUD;
            case CRIT -> ParticleTypes.CRIT;
            case MAGIC_CRIT -> ParticleTypes.ENCHANTED_HIT;
            case END_ROD -> ParticleTypes.END_ROD;
            case SPARK, ELECTRIC -> ParticleTypes.ELECTRIC_SPARK;
            case SNOWFLAKE -> ParticleTypes.SNOWFLAKE;
            case CHERRY -> ParticleTypes.CHERRY_LEAVES;
            case HAPPY -> ParticleTypes.HAPPY_VILLAGER;
            case PORTAL -> ParticleTypes.PORTAL;
            case REVERSE_PORTAL -> ParticleTypes.REVERSE_PORTAL;
            case SOUL -> ParticleTypes.SOUL;
            case SWEEP -> ParticleTypes.SWEEP_ATTACK;
            case EXPLOSION -> ParticleTypes.EXPLOSION;
            case EXPLOSION_BIG -> ParticleTypes.EXPLOSION_EMITTER;
            case DRAGON_BREATH -> PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0f);
            case GLOW -> ParticleTypes.GLOW;
            case WITCH -> ParticleTypes.WITCH;
            case DAMAGE -> ParticleTypes.DAMAGE_INDICATOR;
            case LAVA -> ParticleTypes.LAVA;
            case ASH -> ParticleTypes.ASH;
            case WHITE_ASH -> ParticleTypes.WHITE_ASH;
            case TOTEM -> ParticleTypes.TOTEM_OF_UNDYING;
            case FIREWORK -> ParticleTypes.FIREWORK;
            case SONIC_BOOM -> ParticleTypes.SONIC_BOOM;
            case ENCHANT -> ParticleTypes.ENCHANT;
            case SCULK_SOUL -> ParticleTypes.SCULK_SOUL;
            case DRIP_LAVA -> ParticleTypes.DRIPPING_LAVA;
            case POOF -> ParticleTypes.POOF;
            case WAX_ON -> ParticleTypes.WAX_ON;
        };
    }

    public static void particles(ServerLevel level, Fx.P p, double x, double y, double z, int count, double dx, double dy,
                                 double dz, double speed) {
        level.sendParticles(particle(p), x, y, z, count, dx, dy, dz, speed);
    }

    public static void particlesFor(ServerPlayer player, Fx.P p, double x, double y, double z, int count, double dx,
                                    double dy, double dz, double speed) {
        ((ServerLevel) player.level()).sendParticles(player, particle(p), true, false, x, y, z, count, dx, dy, dz, speed);
    }

    private static DustParticleOptions dustOf(int rgb, float size) {
        return new DustParticleOptions(rgb & 0xFFFFFF, Math.max(0.01f, Math.min(4f, size)));
    }

    public static void dust(ServerLevel level, int rgb, float size, double x, double y, double z, int count, double spread) {
        level.sendParticles(dustOf(rgb, size), x, y, z, count, spread, spread, spread, 0);
    }

    public static void dustFor(ServerPlayer player, int rgb, float size, double x, double y, double z, int count,
                               double spreadXZ, double spreadY) {
        ((ServerLevel) player.level()).sendParticles(player, dustOf(rgb, size), true, false, x, y, z, count, spreadXZ, spreadY,
                spreadXZ, 0);
    }

    private static SoundEvent sound(Fx.S s) {
        return switch (s) {
            case SWING -> SoundEvents.PLAYER_ATTACK_SWEEP;
            case HEAVY_SWING -> SoundEvents.PLAYER_ATTACK_STRONG;
            case HIT_CRIT -> SoundEvents.PLAYER_ATTACK_CRIT;
            case BLADE_DRAW -> SoundEvents.TRIDENT_RETURN;
            case SLAM -> SoundEvents.MACE_SMASH_GROUND_HEAVY;
            case ROAR -> SoundEvents.RAVAGER_ROAR;
            case THUNDER -> SoundEvents.TRIDENT_THUNDER.value();
            case FIRE_WHOOSH -> SoundEvents.BLAZE_SHOOT;
            case FROST -> SoundEvents.GLASS_BREAK;
            case MAGIC_CAST -> SoundEvents.EVOKER_CAST_SPELL;
            case HEAL -> SoundEvents.BEACON_POWER_SELECT;
            case SHRINE_KINDLE -> SoundEvents.BEACON_ACTIVATE;
            case SHRINE_REST -> SoundEvents.RESPAWN_ANCHOR_SET_SPAWN;
            case LEVEL_UP -> SoundEvents.PLAYER_LEVELUP;
            case SMITH -> SoundEvents.ANVIL_USE;
            case BUY -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case BOSS_SPAWN -> SoundEvents.ENDER_DRAGON_GROWL;
            case BOSS_DEATH -> SoundEvents.WITHER_DEATH;
            case DODGE -> SoundEvents.PHANTOM_FLAP;
            case BLEED -> SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH;
            case TELEPORT -> SoundEvents.ENDERMAN_TELEPORT;
            case RUNES_PICKUP, CHIME -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case DRINK -> SoundEvents.GENERIC_DRINK.value();
            case EXPLODE -> SoundEvents.GENERIC_EXPLODE.value();
            case BEAM -> SoundEvents.WARDEN_SONIC_BOOM;
            case DENY -> SoundEvents.VILLAGER_NO;
            case WITHER -> SoundEvents.WITHER_SHOOT;
            case GRAVITY -> SoundEvents.ILLUSIONER_CAST_SPELL;
            case SHARD -> SoundEvents.END_PORTAL_FRAME_FILL;
        };
    }

    public static void sound(Level level, double x, double y, double z, Fx.S s, float volume, float pitch) {
        level.playSound(null, x, y, z, sound(s), SoundSource.PLAYERS, volume, pitch);
    }

    // ------------------------------------------------------------------ players

    public static void actionBar(ServerPlayer player, Component message) {
        player.sendOverlayMessage(message);
    }

    public static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            ItemEntity drop = new ItemEntity(player.level(), player.getX(), player.getY() + 0.5, player.getZ(), stack);
            drop.setNoPickUpDelay();
            player.level().addFreshEntity(drop);
        }
    }

    public static void teleport(ServerPlayer player, ServerLevel level, double x, double y, double z, float yaw, float pitch) {
        player.teleportTo(level, x, y, z, Set.of(), yaw, pitch, false);
    }

    public static void setRespawn(ServerPlayer player, ServerLevel level, BlockPos pos, float yaw) {
        player.setRespawnPosition(new ServerPlayer.RespawnConfig(
                new LevelData.RespawnData(GlobalPos.of(level.dimension(), pos), yaw, 0f), true), false);
    }

    public static void itemCooldown(Player player, ItemStack stack, int ticks) {
        if (!stack.isEmpty()) {
            player.getCooldowns().addCooldown(stack, ticks);
        }
    }

    public static int selectedSlot(Player player) {
        return player.getInventory().getSelectedSlot();
    }

    // ------------------------------------------------------------------ item stack data

    private static CompoundTag root(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag().getCompoundOrEmpty(STACK_ROOT);
    }

    public static int stackInt(ItemStack stack, String key, int fallback) {
        CompoundTag tag = root(stack);
        return tag.getIntOr(key, fallback);
    }

    public static void setStackInt(ItemStack stack, String key, int value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            CompoundTag r = tag.getCompoundOrEmpty(STACK_ROOT);
            r.putInt(key, value);
            tag.put(STACK_ROOT, r);
        });
    }

    public static String stackString(ItemStack stack, String key, String fallback) {
        CompoundTag tag = root(stack);
        return tag.getStringOr(key, fallback);
    }

    public static void setStackString(ItemStack stack, String key, String value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            CompoundTag r = tag.getCompoundOrEmpty(STACK_ROOT);
            r.putString(key, value);
            tag.put(STACK_ROOT, r);
        });
    }

    /** Implicit weapon class for vanilla and third-party melee weapons, or null. */
    public static WeaponClass vanillaWeaponClass(ItemStack stack) {
        if (stack.is(ItemTags.SWORDS)) {
            return WeaponClass.STRAIGHT_SWORD;
        }
        if (stack.is(ItemTags.AXES)) {
            return WeaponClass.AXE;
        }
        if (stack.is(ItemTags.SPEARS) || stack.getItem() instanceof TridentItem) {
            return WeaponClass.SPEAR;
        }
        if (stack.getItem() instanceof MaceItem) {
            return WeaponClass.HAMMER;
        }
        return null;
    }

    /** Total main-hand attack damage the stack grants, including the player's base of 1. */
    public static double attackDamage(ItemStack stack) {
        ItemAttributeModifiers mods = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        double total = 1.0;
        for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)
                    && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE
                    && entry.slot().test(EquipmentSlot.MAINHAND)) {
                total += entry.modifier().amount();
            }
        }
        return total;
    }

    public static ItemStack vanillaSword() {
        return new ItemStack(Items.IRON_SWORD);
    }

    // ------------------------------------------------------------------ commands

    public static boolean isOperator(CommandSourceStack source) {
        return source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public static void feedback(CommandSourceStack source, Component message) {
        source.sendSuccess(() -> message, false);
    }

    private Compat() {
    }
}
