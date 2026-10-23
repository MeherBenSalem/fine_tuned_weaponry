package io.nightbeam.runeborn.compat;

import com.mojang.brigadier.ParseResults;
import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.weapon.WeaponClass;
import io.nightbeam.runeborn.mc.Fx;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Minecraft 1.20.1 implementation of every call that differs between supported versions.
 * The same class, with the same signatures, exists in each version workspace.
 */
public final class Compat {
    private static final String STACK_ROOT = Rb.MOD_ID;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(Rb.MOD_ID, path);
    }

    // ------------------------------------------------------------------ ids and world

    public static String entityTypeId(Entity e) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
    }

    public static String dimensionId(Level level) {
        return level.dimension().location().toString();
    }

    public static String biomeId(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey().map(k -> k.location().toString()).orElse("minecraft:plains");
    }

    public static ServerLevel level(MinecraftServer server, String dimension) {
        ResourceLocation loc = ResourceLocation.tryParse(dimension);
        return loc == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, loc));
    }

    public static BlockPos spawnPos(ServerLevel level) {
        return level.getSharedSpawnPos();
    }

    public static int surface(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    public static boolean isNight(ServerLevel level) {
        return level.isNight();
    }

    // ------------------------------------------------------------------ entities

    public static Set<String> tags(Entity e) {
        return e.getTags();
    }

    public static Entity create(ServerLevel level, String typeId) {
        ResourceLocation loc = ResourceLocation.tryParse(typeId);
        if (loc == null) {
            return null;
        }
        return BuiltInRegistries.ENTITY_TYPE.getOptional(loc).map(t -> (Entity) t.create(level)).orElse(null);
    }

    public static void push(Entity e, Vec3 velocity) {
        e.setDeltaMovement(velocity);
        e.hurtMarked = true;
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
        rider.startRiding(mount, true);
    }

    public static boolean pvpAllowed(LivingEntity attacker) {
        MinecraftServer server = attacker.getServer();
        return server != null && server.isPvpAllowed();
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
        return target.hurt(source, amount);
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
        return e.getMobType() == MobType.UNDEAD;
    }

    public static void ignite(Entity e, int seconds) {
        e.setSecondsOnFire(seconds);
    }

    public static void lightning(ServerLevel level, Vec3 pos, boolean visualOnly) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(pos.x, pos.y, pos.z);
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

    private static MobEffect effect(Fx.E e) {
        return switch (e) {
            case SLOWNESS -> MobEffects.MOVEMENT_SLOWDOWN;
            case POISON -> MobEffects.POISON;
            case WITHER -> MobEffects.WITHER;
            case REGENERATION -> MobEffects.REGENERATION;
            case RESISTANCE -> MobEffects.DAMAGE_RESISTANCE;
            case STRENGTH -> MobEffects.DAMAGE_BOOST;
            case SPEED -> MobEffects.MOVEMENT_SPEED;
            case WEAKNESS -> MobEffects.WEAKNESS;
            case BLINDNESS -> MobEffects.BLINDNESS;
            case DARKNESS -> MobEffects.DARKNESS;
            case FIRE_RESISTANCE -> MobEffects.FIRE_RESISTANCE;
            case ABSORPTION -> MobEffects.ABSORPTION;
            case GLOWING -> MobEffects.GLOWING;
            case LEVITATION -> MobEffects.LEVITATION;
            case SLOW_FALLING -> MobEffects.SLOW_FALLING;
            case NAUSEA -> MobEffects.CONFUSION;
            case HASTE -> MobEffects.DIG_SPEED;
            case MINING_FATIGUE -> MobEffects.DIG_SLOWDOWN;
        };
    }

    /** {@code ticks < 0} means infinite. */
    public static void addEffect(LivingEntity e, Fx.E effect, int ticks, int amplifier) {
        int duration = ticks < 0 ? MobEffectInstance.INFINITE_DURATION : ticks;
        e.addEffect(new MobEffectInstance(effect(effect), duration, amplifier, false, ticks >= 0));
    }

    public static void clearHarmful(LivingEntity e) {
        List<MobEffect> remove = new ArrayList<>();
        for (MobEffectInstance inst : e.getActiveEffects()) {
            if (inst.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                remove.add(inst.getEffect());
            }
        }
        remove.forEach(e::removeEffect);
        e.clearFire();
    }

    // ------------------------------------------------------------------ attributes

    /** Null for attributes this version lacks (scale; reach on Fabric). */
    private static Attribute attribute(Fx.A a) {
        return switch (a) {
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case KNOCKBACK_RESISTANCE -> Attributes.KNOCKBACK_RESISTANCE;
            case LUCK -> Attributes.LUCK;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case ARMOR -> Attributes.ARMOR;
            case SCALE -> null;
            case ENTITY_REACH -> Platform.entityReach();
            case FOLLOW_RANGE -> Attributes.FOLLOW_RANGE;
        };
    }

    /** Sets (replacing) a permanent modifier. {@code multiplyBase} adds {@code amount * base}. */
    public static void setModifier(LivingEntity e, Fx.A a, String id, double amount, boolean multiplyBase) {
        AttributeInstance inst = instance(e, a);
        if (inst == null) {
            return;
        }
        UUID uuid = modifierId(id);
        inst.removeModifier(uuid);
        if (amount != 0) {
            inst.addPermanentModifier(new AttributeModifier(uuid, Rb.MOD_ID + ":" + id, amount,
                    multiplyBase ? AttributeModifier.Operation.MULTIPLY_BASE : AttributeModifier.Operation.ADDITION));
        }
    }

    /** Stable modifier UUID per Runeborn modifier name. */
    public static UUID modifierId(String id) {
        return UUID.nameUUIDFromBytes((Rb.MOD_ID + ":" + id).getBytes(StandardCharsets.UTF_8));
    }

    private static AttributeInstance instance(LivingEntity e, Fx.A a) {
        Attribute attr = attribute(a);
        return attr == null ? null : e.getAttribute(attr);
    }

    public static void setBase(LivingEntity e, Fx.A a, double value) {
        AttributeInstance inst = instance(e, a);
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    public static double attrBase(LivingEntity e, Fx.A a) {
        AttributeInstance inst = instance(e, a);
        return inst == null ? 0 : inst.getBaseValue();
    }

    public static double attrValue(LivingEntity e, Fx.A a) {
        AttributeInstance inst = instance(e, a);
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
            case DRAGON_BREATH -> ParticleTypes.DRAGON_BREATH;
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
        ((ServerLevel) player.level()).sendParticles(player, particle(p), true, x, y, z, count, dx, dy, dz, speed);
    }

    private static DustParticleOptions dustOf(int rgb, float size) {
        return new DustParticleOptions(new Vector3f(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f,
                (rgb & 0xFF) / 255f), Math.max(0.01f, Math.min(4f, size)));
    }

    public static void dust(ServerLevel level, int rgb, float size, double x, double y, double z, int count, double spread) {
        level.sendParticles(dustOf(rgb, size), x, y, z, count, spread, spread, spread, 0);
    }

    public static void dustFor(ServerPlayer player, int rgb, float size, double x, double y, double z, int count,
                               double spreadXZ, double spreadY) {
        ((ServerLevel) player.level()).sendParticles(player, dustOf(rgb, size), true, x, y, z, count, spreadXZ, spreadY,
                spreadXZ, 0);
    }

    private static SoundEvent sound(Fx.S s) {
        return switch (s) {
            case SWING -> SoundEvents.PLAYER_ATTACK_SWEEP;
            case HEAVY_SWING -> SoundEvents.PLAYER_ATTACK_STRONG;
            case HIT_CRIT -> SoundEvents.PLAYER_ATTACK_CRIT;
            case BLADE_DRAW -> SoundEvents.TRIDENT_RETURN;
            case SLAM -> SoundEvents.WARDEN_ATTACK_IMPACT;
            case ROAR -> SoundEvents.RAVAGER_ROAR;
            case THUNDER -> SoundEvents.TRIDENT_THUNDER;
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
            case DRINK -> SoundEvents.GENERIC_DRINK;
            case EXPLODE -> SoundEvents.GENERIC_EXPLODE;
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
        player.displayClientMessage(message, true);
    }

    public static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            player.drop(stack, false);
        }
    }

    public static void teleport(ServerPlayer player, ServerLevel level, double x, double y, double z, float yaw, float pitch) {
        player.teleportTo(level, x, y, z, yaw, pitch);
    }

    public static void setRespawn(ServerPlayer player, ServerLevel level, BlockPos pos, float yaw) {
        player.setRespawnPosition(level.dimension(), pos, yaw, true, false);
    }

    public static void itemCooldown(Player player, ItemStack stack, int ticks) {
        if (!stack.isEmpty()) {
            player.getCooldowns().addCooldown(stack.getItem(), ticks);
        }
    }

    public static int selectedSlot(Player player) {
        return player.getInventory().selected;
    }

    // ------------------------------------------------------------------ item stack data

    private static CompoundTag root(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(STACK_ROOT);
        return tag == null ? new CompoundTag() : tag;
    }

    public static int stackInt(ItemStack stack, String key, int fallback) {
        CompoundTag tag = root(stack);
        return tag.contains(key) ? tag.getInt(key) : fallback;
    }

    public static void setStackInt(ItemStack stack, String key, int value) {
        stack.getOrCreateTagElement(STACK_ROOT).putInt(key, value);
    }

    public static String stackString(ItemStack stack, String key, String fallback) {
        CompoundTag tag = root(stack);
        return tag.contains(key) ? tag.getString(key) : fallback;
    }

    public static void setStackString(ItemStack stack, String key, String value) {
        stack.getOrCreateTagElement(STACK_ROOT).putString(key, value);
    }

    /** Implicit weapon class for vanilla and third-party melee weapons, or null. */
    public static WeaponClass vanillaWeaponClass(ItemStack stack) {
        var item = stack.getItem();
        if (item instanceof SwordItem) {
            return WeaponClass.STRAIGHT_SWORD;
        }
        if (item instanceof AxeItem) {
            return WeaponClass.AXE;
        }
        if (item instanceof TridentItem) {
            return WeaponClass.SPEAR;
        }
        return null;
    }

    /** Total main-hand attack damage the stack grants, including the player's base of 1. */
    public static double attackDamage(ItemStack stack) {
        double total = 1.0;
        for (AttributeModifier mod : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
                total += mod.getAmount();
            }
        }
        return total;
    }

    public static ItemStack vanillaSword() {
        return new ItemStack(Items.IRON_SWORD);
    }

    // ------------------------------------------------------------------ commands

    public static boolean isOperator(CommandSourceStack source) {
        return source.hasPermission(2);
    }

    public static void feedback(CommandSourceStack source, Component message) {
        source.sendSuccess(() -> message, false);
    }

    // ------------------------------------------------------------------ integrations, advancements, recipes

    /** Runs a command as the server with output suppressed. Returns false if it does not parse. */
    public static boolean runCommand(ServerPlayer player, String command) {
        MinecraftServer server = player.server;
        CommandSourceStack source = server.createCommandSourceStack().withSuppressedOutput();
        ParseResults<CommandSourceStack> parse = server.getCommands().getDispatcher().parse(command, source);
        if (parse.getReader().canRead() || !parse.getExceptions().isEmpty()) {
            return false;
        }
        server.getCommands().performCommand(parse, command);
        return true;
    }

    /** Awards the single criterion of a Runeborn advancement, if it is loaded. */
    public static void grantAdvancement(ServerPlayer player, String path) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(id(path));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "granted");
        }
    }

    /** Puts Runeborn recipes in the player's recipe book. */
    public static void unlockRecipes(ServerPlayer player, List<String> recipes) {
        ResourceLocation[] ids = new ResourceLocation[recipes.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = id(recipes.get(i));
        }
        player.awardRecipesByKey(ids);
    }

    private Compat() {
    }
}
