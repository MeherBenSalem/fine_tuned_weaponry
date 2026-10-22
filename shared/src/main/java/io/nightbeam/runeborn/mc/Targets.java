package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Target selection geometry for arts, spells and boss attacks. */
public final class Targets {
    /** Whether an area attack from {@code attacker} may hit {@code e}. Spares pets, villagers and allies. */
    public static boolean hostile(LivingEntity attacker, Entity e) {
        if (!(e instanceof LivingEntity living) || e == attacker || !living.isAlive() || Compat.isIgnoredTarget(e)) {
            return false;
        }
        if (e.isAlliedTo(attacker) || attacker.isAlliedTo(e)) {
            return false;
        }
        if (e instanceof Player p) {
            if (p.isCreative() || p.isSpectator()) {
                return false;
            }
            if (attacker instanceof Player) {
                return Compat.pvpAllowed(attacker);
            }
            return true;
        }
        if (attacker instanceof Player) {
            if (e instanceof Enemy) {
                return true;
            }
            return e instanceof Mob mob && mob.getTarget() == attacker;
        }
        // Mobs (bosses) hit players and anything fighting them.
        return e instanceof Mob mob && attacker instanceof Mob self && self.getTarget() == mob;
    }

    public static List<LivingEntity> around(LivingEntity source, Vec3 center, double radius) {
        ServerLevel level = (ServerLevel) source.level();
        AABB box = new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius,
                center.y + radius, center.z + radius);
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, e -> hostile(source, e))) {
            if (e.getBoundingBox().getCenter().distanceToSqr(center) <= (radius + e.getBbWidth()) * (radius + e.getBbWidth())) {
                out.add(e);
            }
        }
        return out;
    }

    /** Horizontal cone in front of {@code source}. */
    public static List<LivingEntity> cone(LivingEntity source, double range, double degrees) {
        Vec3 origin = source.position();
        Vec3 look = flatLook(source);
        double cos = Math.cos(Math.toRadians(degrees / 2));
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : around(source, origin.add(0, source.getBbHeight() * 0.5, 0), range + 1)) {
            Vec3 to = e.position().subtract(origin);
            Vec3 flat = new Vec3(to.x, 0, to.z);
            double len = flat.length();
            if (len < 1.2 || flat.normalize().dot(look) >= cos) {
                if (len <= range + e.getBbWidth()) {
                    out.add(e);
                }
            }
        }
        return out;
    }

    /** Everything a segment from the eyes passes through, thickened by {@code width}. */
    public static List<LivingEntity> line(LivingEntity source, double range, double width) {
        Vec3 from = source.getEyePosition();
        Vec3 to = from.add(source.getLookAngle().scale(range));
        return segment(source, from, to, width);
    }

    public static List<LivingEntity> segment(LivingEntity source, Vec3 from, Vec3 to, double width) {
        ServerLevel level = (ServerLevel) source.level();
        AABB box = new AABB(from, to).inflate(width + 1);
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, e -> hostile(source, e))) {
            AABB bb = e.getBoundingBox().inflate(width);
            if (bb.contains(from) || bb.clip(from, to).isPresent()) {
                out.add(e);
            }
        }
        out.sort(Comparator.comparingDouble(e -> e.distanceToSqr(from)));
        return out;
    }

    /** First valid entity along the look vector, or null. Blocks stop the ray. */
    public static LivingEntity ray(LivingEntity source, double range) {
        Vec3 from = source.getEyePosition();
        Vec3 end = point(source, range);
        List<LivingEntity> hits = segment(source, from, end, 0.35);
        return hits.isEmpty() ? null : hits.get(0);
    }

    /** Where the look vector meets a block, or the end of the range. */
    public static Vec3 point(LivingEntity source, double range) {
        Vec3 from = source.getEyePosition();
        Vec3 to = from.add(source.getLookAngle().scale(range));
        BlockHitResult hit = source.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, source));
        return hit.getType() == HitResult.Type.MISS ? to : hit.getLocation();
    }

    /** Target point for ground effects: an entity under the crosshair, else the block hit. */
    public static Vec3 aim(LivingEntity source, double range) {
        LivingEntity target = ray(source, range);
        if (target != null) {
            return target.position();
        }
        Vec3 p = point(source, range);
        return ground(source, p);
    }

    /** Snaps a point down to the ground below it (within 8 blocks). */
    public static Vec3 ground(LivingEntity source, Vec3 p) {
        Vec3 down = p.add(0, -8, 0);
        BlockHitResult hit = source.level().clip(new ClipContext(p.add(0, 0.5, 0), down, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, source));
        return hit.getType() == HitResult.Type.MISS ? p : hit.getLocation();
    }

    public static Vec3 flatLook(Entity e) {
        double yaw = Math.toRadians(e.getYRot());
        return new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    public static List<LivingEntity> nearest(LivingEntity source, double range, int max, double coneDegrees) {
        List<LivingEntity> list = coneDegrees >= 360 ? around(source, source.position(), range) : cone(source, range, coneDegrees);
        list.sort(Comparator.comparingDouble(e -> e.distanceToSqr(source)));
        return list.size() > max ? new ArrayList<>(list.subList(0, max)) : list;
    }

    public static Optional<Vec3> clip(AABB box, Vec3 from, Vec3 to) {
        return box.clip(from, to);
    }

    private Targets() {
    }
}
