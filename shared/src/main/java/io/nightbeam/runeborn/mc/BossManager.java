package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.Ability;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.Loot;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.weapon.StatusType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/** Sovereigns: summoning, scripted abilities, phase two, boss bars and rewards. */
public final class BossManager {
    public static final String TAG = "runeborn.boss.";
    private static final Map<UUID, Boss> ACTIVE = new HashMap<>();
    private static final Set<UUID> BAR_VIEWERS = new HashSet<>();
    private static final Random RANDOM = new Random();

    private static final class Boss {
        final LivingEntity entity;
        final BossSpec spec;
        int phase = 1;
        long nextAt;
        final Map<Ability, Long> ready = new HashMap<>();
        final Set<UUID> fighters = new HashSet<>();

        Boss(LivingEntity entity, BossSpec spec) {
            this.entity = entity;
            this.spec = spec;
        }
    }

    public static BossSpec specOf(Entity e) {
        for (String tag : Compat.tags(e)) {
            if (tag.startsWith(TAG)) {
                return Bosses.get(tag.substring(TAG.length()));
            }
        }
        return Bosses.vanilla(Compat.entityTypeId(e));
    }

    public static boolean isBoss(Entity e) {
        return ACTIVE.containsKey(e.getUUID()) || specOf(e) != null;
    }

    public static void onLoad(Entity e) {
        if (e instanceof LivingEntity living && !ACTIVE.containsKey(e.getUUID())) {
            BossSpec spec = specOf(e);
            if (spec != null) {
                ACTIVE.put(e.getUUID(), new Boss(living, spec));
            }
        }
    }

    // ------------------------------------------------------------------ Summoning

    public static boolean summon(ServerPlayer player, BossSpec spec) {
        ServerLevel level = (ServerLevel) player.level();
        for (Boss b : ACTIVE.values()) {
            if (b.spec == spec && b.entity.isAlive() && b.entity.level() == level && b.entity.distanceToSqr(player) < 96 * 96) {
                Players.actionBar(player, Lang.MSG_BOSS_ACTIVE);
                return false;
            }
        }
        Vec3 at = Targets.ground(player, player.position().add(Targets.flatLook(player).scale(7)).add(0, 3, 0));
        if (spawn(level, spec, at, player) == null) {
            Players.actionBar(player, Lang.MSG_NOT_SUMMONABLE);
            return false;
        }
        return true;
    }

    /** Spawns a boss at {@code at}, aggressive towards {@code target} (may be null). Returns the boss or null. */
    public static LivingEntity spawn(ServerLevel level, BossSpec spec, Vec3 at, ServerPlayer target) {
        String[] types = spec.entity().split("\\+");
        Entity created = Compat.create(level, types[0]);
        if (!(created instanceof Mob mob)) {
            return null;
        }
        mob.setPos(at.x, at.y, at.z);
        mob.addTag(Mobs.SEEN);
        mob.addTag(TAG + spec.id());
        mob.addTag(Mobs.LEVEL + spec.level());
        Compat.setBase(mob, Fx.A.MAX_HEALTH, spec.health());
        Compat.setBase(mob, Fx.A.ATTACK_DAMAGE, spec.damage());
        Compat.setBase(mob, Fx.A.ARMOR, spec.armor());
        Compat.setBase(mob, Fx.A.MOVEMENT_SPEED, spec.speed());
        Compat.setBase(mob, Fx.A.FOLLOW_RANGE, 48);
        Compat.setBase(mob, Fx.A.KNOCKBACK_RESISTANCE, 0.85);
        Compat.setModifier(mob, Fx.A.SCALE, "boss_scale", 0.4, true);
        mob.setHealth(mob.getMaxHealth());
        mob.setCustomName(Component.literal(spec.name()).withStyle(ChatFormatting.GOLD));
        mob.setPersistenceRequired();
        if (!spec.weapon().isEmpty()) {
            ItemStack weapon = RbItems.stack(spec.weapon(), 1);
            Compat.equip(mob, weapon);
        }
        if ("morgrath".equals(spec.id())) {
            Compat.addEffect(mob, Fx.E.FIRE_RESISTANCE, -1, 0);
        }
        Compat.prepareBoss(mob);
        level.addFreshEntity(mob);
        if (types.length > 1) {
            Entity mount = Compat.create(level, types[1]);
            if (mount instanceof Mob horse) {
                horse.setPos(at.x, at.y, at.z);
                horse.addTag(Mobs.SEEN);
                Compat.setBase(horse, Fx.A.MAX_HEALTH, 80);
                Compat.setBase(horse, Fx.A.MOVEMENT_SPEED, 0.3);
                horse.setHealth(horse.getMaxHealth());
                horse.setPersistenceRequired();
                level.addFreshEntity(horse);
                Compat.ride(mob, horse);
            }
        }
        Boss boss = new Boss(mob, spec);
        boss.nextAt = RbServer.now() + 60;
        if (target != null) {
            mob.setTarget(target);
            boss.fighters.add(target.getUUID());
        }
        ACTIVE.put(mob.getUUID(), boss);

        Compat.lightning(level, at, true);
        Fx.burst(level, Fx.P.LARGE_SMOKE, at.add(0, 1, 0), 60, 1.2, 0.05);
        Fx.burst(level, Fx.P.SOUL_FIRE, at.add(0, 1, 0), 40, 0.8, 0.1);
        Compat.sound(level, at.x, at.y, at.z, Fx.S.BOSS_SPAWN, 2f, 0.8f);
        for (ServerPlayer p : level.players()) {
            if (p.distanceToSqr(at) < 64 * 64) {
                Players.banner(p, Packets.Banner.BOSS_APPEARS, spec.name(), spec.title());
                Players.send(p, Packets.fx(Packets.Fx.SHAKE, 1f));
            }
        }
        return mob;
    }

    /** Night-time field boss for travellers far from spawn. Checked every 200 ticks. */
    private static void maybeSpawnDuskRider(net.minecraft.server.MinecraftServer server) {
        ServerConfig cfg = ServerConfig.INSTANCE;
        if (!cfg.duskRiderSpawns) {
            return;
        }
        BossSpec spec = Bosses.get("dusk_rider");
        ServerLevel level = Compat.level(server, "minecraft:overworld");
        if (level == null || !Compat.isNight(level)) {
            return;
        }
        for (Boss b : ACTIVE.values()) {
            if (b.spec == spec) {
                return;
            }
        }
        BlockPos spawn = Compat.spawnPos(level);
        for (ServerPlayer p : level.players()) {
            if (p.isCreative() || p.isSpectator() || p.blockPosition().distSqr(spawn) < 300 * 300) {
                continue;
            }
            if (RANDOM.nextDouble() < cfg.duskRiderNightlyChance / 60.0) {
                double angle = RANDOM.nextDouble() * Math.PI * 2;
                int x = (int) (p.getX() + Math.cos(angle) * 32);
                int z = (int) (p.getZ() + Math.sin(angle) * 32);
                spawn(level, spec, new Vec3(x + 0.5, Compat.surface(level, x, z), z + 0.5), p);
                return;
            }
        }
    }

    // ------------------------------------------------------------------ Ticking

    public static void tick(net.minecraft.server.MinecraftServer server, long now) {
        Iterator<Boss> it = ACTIVE.values().iterator();
        while (it.hasNext()) {
            Boss b = it.next();
            if (b.entity.isRemoved()) {
                it.remove();
                continue;
            }
            if (!b.entity.isAlive() || b.spec.abilities().isEmpty() || !(b.entity instanceof Mob mob) || mob.isNoAi()) {
                continue; // a NoAI boss (posed by a map maker or a script) stays still and casts nothing
            }
            if (b.phase == 1 && b.entity.getHealth() <= b.entity.getMaxHealth() * 0.5f) {
                b.phase = 2;
                if (b.spec.abilities().contains(Ability.ENRAGE)) {
                    enrage(b);
                }
            }
            if (now >= b.nextAt) {
                LivingEntity target = mob.getTarget();
                if (target != null && target.isAlive() && target.distanceToSqr(mob) < 32 * 32) {
                    Ability a = pick(b, now, target);
                    if (a != null) {
                        perform(b, mob, target, a);
                        b.ready.put(a, now + Math.round(a.cooldown * (b.phase == 2 ? 0.7 : 1.0)));
                        b.nextAt = now + (b.phase == 2 ? 30 : 45);
                    }
                } else if (target == null) {
                    retarget(b, mob);
                }
            }
        }
        if (now % 10 == 0) {
            bars(server);
        }
        if (now % 200 == 0) {
            maybeSpawnDuskRider(server);
        }
    }

    private static void retarget(Boss b, Mob mob) {
        ServerLevel level = (ServerLevel) mob.level();
        ServerPlayer best = null;
        double bestD = 40 * 40;
        for (ServerPlayer p : level.players()) {
            double dist = p.distanceToSqr(mob);
            if (!p.isCreative() && !p.isSpectator() && dist < bestD) {
                best = p;
                bestD = dist;
            }
        }
        if (best != null) {
            mob.setTarget(best);
        }
    }

    private static Ability pick(Boss b, long now, LivingEntity target) {
        List<Ability> options = new ArrayList<>();
        double dist = Math.sqrt(target.distanceToSqr(b.entity));
        for (Ability a : b.spec.abilities()) {
            if (a == Ability.ENRAGE || a.minPhase > b.phase || b.ready.getOrDefault(a, 0L) > now) {
                continue;
            }
            boolean melee = a == Ability.GROUND_SLAM || a == Ability.BLOOD_FLURRY;
            if (melee && dist > 6) {
                continue;
            }
            options.add(a);
        }
        return options.isEmpty() ? null : options.get(RANDOM.nextInt(options.size()));
    }

    private static void enrage(Boss b) {
        LivingEntity e = b.entity;
        ServerLevel level = (ServerLevel) e.level();
        Compat.addEffect(e, Fx.E.STRENGTH, -1, 1);
        Compat.addEffect(e, Fx.E.SPEED, -1, 0);
        Compat.addEffect(e, Fx.E.RESISTANCE, -1, 0);
        Fx.burst(level, Fx.P.FLAME, e, 80, 0.2);
        Fx.burst(level, Fx.P.SOUL_FIRE, e, 40, 0.1);
        Fx.sound(e, Fx.S.ROAR, 2f, 0.6f);
        for (ServerPlayer p : level.players()) {
            if (p.distanceToSqr(e) < 48 * 48) {
                Players.send(p, Packets.fx(Packets.Fx.FLASH_RED, 1f));
                Players.send(p, Packets.fx(Packets.Fx.SHAKE, 1f));
            }
        }
    }

    private static void perform(Boss b, Mob boss, LivingEntity target, Ability a) {
        ServerLevel level = (ServerLevel) boss.level();
        double dmg = Compat.attrValue(boss, Fx.A.ATTACK_DAMAGE);
        switch (a) {
            case GROUND_SLAM -> telegraphRing(level, boss.position(), 5, 14, Fx.P.CRIT, () -> {
                Compat.blockBurst(level, boss.position(), 5, 60);
                Fx.ring(level, Fx.P.EXPLOSION, boss.position().add(0, 0.2, 0), 4, 12);
                Fx.sound(boss, Fx.S.SLAM, 2f, 0.6f);
                for (LivingEntity e : Targets.around(boss, boss.position(), 5)) {
                    Combat.hit(boss, e, dmg * 1.4, Combat.Kind.PHYSICAL);
                    Compat.push(e, e.position().subtract(boss.position()).normalize().scale(0.8).add(0, 0.7, 0));
                }
            });
            case SUMMON_HOLLOWS -> {
                Fx.sound(boss, Fx.S.ROAR, 1.5f, 1.2f);
                for (int i = 0; i < 3; i++) {
                    Entity add = Compat.create(level, "minecraft:zombie");
                    if (add instanceof Mob z) {
                        double ang = RANDOM.nextDouble() * Math.PI * 2;
                        z.setPos(boss.getX() + Math.cos(ang) * 3, boss.getY(), boss.getZ() + Math.sin(ang) * 3);
                        z.addTag(Mobs.SEEN);
                        z.addTag(Mobs.LEVEL + Math.max(1, b.spec.level() / 2));
                        z.setCustomName(Component.literal("Hollow").withStyle(ChatFormatting.GRAY));
                        level.addFreshEntity(z);
                        z.setTarget(target);
                        Fx.burst(level, Fx.P.SOUL, z, 20, 0.05);
                    }
                }
            }
            case FROST_NOVA -> telegraphRing(level, boss.position(), 7, 16, Fx.P.SNOWFLAKE, () -> {
                Fx.burst(level, Fx.P.SNOWFLAKE, boss.position().add(0, 1, 0), 200, 3.5, 0.2);
                Fx.sound(boss, Fx.S.FROST, 2f, 0.6f);
                for (LivingEntity e : Targets.around(boss, boss.position(), 7)) {
                    Combat.hit(boss, e, dmg * 0.8, Combat.Kind.MAGIC);
                    Status.add(e, StatusType.FROST, 60, boss);
                }
            });
            case ICE_VOLLEY -> {
                for (int i = 0; i < 5; i++) {
                    RbServer.later(i * 5 + 1, () -> {
                        if (!boss.isAlive() || !target.isAlive()) {
                            return;
                        }
                        Fx.line(level, Fx.P.SNOWFLAKE, boss.getEyePosition(), target.getEyePosition(), 0.4);
                        Fx.sound(boss, Fx.S.FROST, 0.8f, 1.6f);
                        if (RANDOM.nextDouble() < 0.7) {
                            Combat.hit(boss, target, dmg * 0.5, Combat.Kind.MAGIC);
                            Status.add(target, StatusType.FROST, 25, boss);
                        }
                    });
                }
            }
            case BLINK -> {
                Vec3 behind = target.position().subtract(Targets.flatLook(target).scale(3));
                Fx.burst(level, Fx.P.PORTAL, boss, 40, 0.5);
                boss.teleportTo(behind.x, target.getY(), behind.z);
                Fx.burst(level, Fx.P.REVERSE_PORTAL, boss, 40, 0.3);
                Fx.sound(boss, Fx.S.TELEPORT, 1f, 0.8f);
            }
            case LIGHTNING_BARRAGE -> {
                for (int i = 0; i < 6; i++) {
                    Vec3 p = target.position().add(RANDOM.nextGaussian() * 3, 0, RANDOM.nextGaussian() * 3);
                    RbServer.later(i * 6, () -> telegraphRing(level, p, 2.5, 14, Fx.P.ELECTRIC, () -> {
                        Compat.lightning(level, p, true);
                        for (LivingEntity e : Targets.around(boss, p, 2.5)) {
                            Combat.hit(boss, e, dmg * 0.9, Combat.Kind.LIGHTNING);
                        }
                    }));
                }
            }
            case STORM_DASH -> {
                Vec3 start = boss.position();
                Vec3 dir = target.position().subtract(start).normalize();
                Compat.push(boss, dir.scale(2.2).add(0, 0.2, 0));
                Fx.sound(boss, Fx.S.THUNDER, 1f, 1.4f);
                RbServer.later(5, () -> {
                    Fx.line(level, Fx.P.ELECTRIC, start.add(0, 1, 0), boss.position().add(0, 1, 0), 0.2);
                    for (LivingEntity e : Targets.segment(boss, start.add(0, 1, 0), boss.position().add(0, 1, 0), 1.3)) {
                        Combat.hit(boss, e, dmg * 1.1, Combat.Kind.LIGHTNING);
                    }
                });
            }
            case BLOOD_FLURRY -> Arts.repeat(3, 5, i -> {
                if (!boss.isAlive()) {
                    return;
                }
                Fx.slash(level, Fx.P.DAMAGE, boss, 3, 110, (i - 1) * 0.3);
                Fx.sound(boss, Fx.S.SWING, 1f, 1.2f);
                for (LivingEntity e : Targets.cone(boss, 4, 110)) {
                    Combat.hit(boss, e, dmg * 0.7, Combat.Kind.PHYSICAL);
                    Status.add(e, StatusType.BLEED, 30, boss);
                }
            }, boss);
            case BLOOD_BLOOM -> telegraphRing(level, boss.position(), 8, 22, Fx.P.DAMAGE, () -> {
                Compat.dust(level, 0xB3262B, 2.2f, boss.getX(), boss.getY() + 1, boss.getZ(), 200, 3.5);
                Fx.sound(boss, Fx.S.BLEED, 2f, 0.6f);
                for (LivingEntity e : Targets.around(boss, boss.position(), 8)) {
                    Combat.hit(boss, e, dmg * 0.6, Combat.Kind.PHYSICAL);
                    Status.add(e, StatusType.BLEED, 70, boss);
                }
            });
            case METEOR_RAIN -> {
                for (int i = 0; i < 6; i++) {
                    Vec3 p = target.position().add(RANDOM.nextGaussian() * 4, 0, RANDOM.nextGaussian() * 4);
                    RbServer.later(i * 7, () -> {
                        for (int t = 0; t < 12; t += 2) {
                            double h = 12 - t;
                            RbServer.later(t, () -> Fx.burst(level, Fx.P.FLAME, p.add(0, h, 0), 6, 0.2, 0.02));
                        }
                        telegraphRing(level, p, 3, 14, Fx.P.FLAME, () -> {
                            Fx.burst(level, Fx.P.EXPLOSION_BIG, p, 1, 0, 0);
                            Compat.sound(level, p.x, p.y, p.z, Fx.S.EXPLODE, 1.2f, 0.9f);
                            for (LivingEntity e : Targets.around(boss, p, 3)) {
                                Combat.hit(boss, e, dmg * 1.1, Combat.Kind.FIRE);
                                Compat.ignite(e, 4);
                            }
                        });
                    });
                }
            }
            case FIRE_PILLARS -> {
                List<LivingEntity> victims = Targets.around(boss, boss.position(), 20);
                for (LivingEntity v : victims.subList(0, Math.min(3, victims.size()))) {
                    Vec3 p = v.position();
                    telegraphRing(level, p, 2.5, 16, Fx.P.SMOKE, () -> {
                        Fx.column(level, Fx.P.FLAME, p, 6, 50, 0.6);
                        Compat.sound(level, p.x, p.y, p.z, Fx.S.FIRE_WHOOSH, 1.2f, 0.7f);
                        for (LivingEntity e : Targets.around(boss, p.add(0, 1, 0), 2.5)) {
                            Combat.hit(boss, e, dmg * 1.2, Combat.Kind.FIRE);
                            Compat.ignite(e, 5);
                            Compat.push(e, new Vec3(0, 0.6, 0));
                        }
                    });
                }
            }
            case ASH_CLOUD -> {
                Fx.burst(level, Fx.P.LARGE_SMOKE, boss.position().add(0, 1.5, 0), 120, 4, 0.05);
                Fx.burst(level, Fx.P.ASH, boss.position().add(0, 1.5, 0), 200, 6, 0.02);
                for (LivingEntity e : Targets.around(boss, boss.position(), 10)) {
                    Compat.addEffect(e, Fx.E.BLINDNESS, 60, 0);
                }
            }
            case CHARGE -> {
                Vec3 dir = target.position().subtract(boss.position()).normalize();
                Fx.sound(boss, Fx.S.ROAR, 1.5f, 1f);
                Set<Integer> hit = new HashSet<>();
                Entity mover = boss.getVehicle() != null ? boss.getVehicle() : boss;
                for (int i = 0; i < 10; i++) {
                    RbServer.later(8 + i, () -> {
                        if (!boss.isAlive()) {
                            return;
                        }
                        Compat.push(mover, dir.scale(0.9));
                        Fx.burst(level, Fx.P.CLOUD, boss.position(), 4, 0.3, 0.01);
                        for (LivingEntity e : Targets.around(boss, boss.position().add(dir), 1.8)) {
                            if (hit.add(e.getId())) {
                                Combat.hit(boss, e, dmg * 1.3, Combat.Kind.PHYSICAL);
                                Compat.push(e, dir.scale(1.4).add(0, 0.5, 0));
                            }
                        }
                    });
                }
            }
            case ROAR -> {
                Fx.sound(boss, Fx.S.ROAR, 2.5f, 0.5f);
                Fx.ring(level, Fx.P.SONIC_BOOM, boss.position().add(0, 1, 0), 3, 8);
                for (LivingEntity e : Targets.around(boss, boss.position(), 12)) {
                    Compat.addEffect(e, Fx.E.SLOWNESS, 50, 2);
                    Compat.addEffect(e, Fx.E.WEAKNESS, 100, 0);
                }
            }
            case NIGHTFALL -> {
                Fx.sound(boss, Fx.S.WITHER, 1.5f, 0.5f);
                for (LivingEntity e : Targets.around(boss, boss.position(), 24)) {
                    Compat.addEffect(e, Fx.E.DARKNESS, 160, 0);
                }
            }
            case ENRAGE -> {
            }
        }
    }

    /** Shows a shrinking ring of particles, then runs the attack: telegraphed, dodgeable damage. */
    private static void telegraphRing(ServerLevel level, Vec3 at, double radius, int delay, Fx.P particle, Runnable then) {
        int steps = 3;
        for (int i = 0; i < steps; i++) {
            double r = radius * (1 - i / (double) (steps + 1));
            RbServer.later(1 + i * delay / steps, () -> Fx.ring(level, particle, at.add(0, 0.15, 0), r, (int) (r * 8)));
        }
        RbServer.later(delay, then);
    }

    // ------------------------------------------------------------------ Bars and rewards

    private static void bars(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            List<Packets.BossBar> list = new ArrayList<>();
            for (Boss b : ACTIVE.values()) {
                if (!b.spec.summonable() || !b.entity.isAlive() || b.entity.level() != p.level()
                        || b.entity.distanceToSqr(p) > 64 * 64) {
                    continue;
                }
                list.add(new Packets.BossBar(b.entity.getId(), b.spec.name(), b.entity.getHealth(), b.entity.getMaxHealth(),
                        b.spec.color(), b.phase));
                if (list.size() >= 3) {
                    break;
                }
            }
            if (!list.isEmpty()) {
                BAR_VIEWERS.add(p.getUUID());
                Players.send(p, Packets.bosses(list));
            } else if (BAR_VIEWERS.remove(p.getUUID())) {
                Players.send(p, Packets.bosses(list));
            }
        }
    }

    static void onDamaged(LivingEntity target, ServerPlayer attacker) {
        Boss b = ACTIVE.get(target.getUUID());
        if (b != null) {
            b.fighters.add(attacker.getUUID());
        }
    }

    /** Handles a boss death; returns false if the entity was not a boss. */
    static boolean onDeath(LivingEntity entity) {
        Boss b = ACTIVE.remove(entity.getUUID());
        BossSpec spec = b != null ? b.spec : specOf(entity);
        if (spec == null) {
            return false;
        }
        ServerLevel level = (ServerLevel) entity.level();
        Set<UUID> fighters = b != null ? b.fighters : new HashSet<>();
        Fx.burst(level, Fx.P.SOUL, entity, 80, 0.1);
        Fx.burst(level, Fx.P.END_ROD, entity, 60, 0.2);
        Compat.sound(level, entity.getX(), entity.getY(), entity.getZ(), Fx.S.BOSS_DEATH, 2f, 1f);
        for (ServerPlayer p : level.players()) {
            if (!fighters.contains(p.getUUID()) && p.distanceToSqr(entity) > 80 * 80) {
                continue;
            }
            PlayerData d = RbServer.data(p);
            boolean first = !d.hasFelled(spec.id());
            if (first) {
                d.felled.add(spec.id());
            }
            Feats.felled(p, spec);
            long runes = Runes.forBoss(ServerConfig.INSTANCE, spec.runes(), fighters.size());
            Players.giveRunes(p, runes);
            for (Loot.Drop drop : Loot.forBoss(spec, RANDOM)) {
                Players.give(p, drop.id(), drop.count());
            }
            Players.banner(p, Packets.Banner.SOVEREIGN_FELLED, Lang.en(Lang.B_FELLED), spec.name());
            Players.send(p, Packets.fx(Packets.Fx.FLASH_GOLD, 1f));
            Players.sync(p);
            RbServer.save(p);
        }
        return true;
    }

    public static void clear() {
        ACTIVE.clear();
        BAR_VIEWERS.clear();
    }

    private BossManager() {
    }
}
