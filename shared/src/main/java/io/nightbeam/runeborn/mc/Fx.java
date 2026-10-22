package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Version-independent names for particles, sounds, effects and attributes. Each version's
 * {@link Compat} maps them onto that version's registries.
 */
public final class Fx {
    public enum P {
        FLAME, SOUL_FIRE, SMOKE, LARGE_SMOKE, CLOUD, CRIT, MAGIC_CRIT, END_ROD, SPARK, SNOWFLAKE, CHERRY, HAPPY,
        PORTAL, REVERSE_PORTAL, SOUL, SWEEP, EXPLOSION, EXPLOSION_BIG, DRAGON_BREATH, GLOW, WITCH, DAMAGE, LAVA, ASH,
        WHITE_ASH, TOTEM, FIREWORK, SONIC_BOOM, ENCHANT, SCULK_SOUL, DRIP_LAVA, POOF, WAX_ON, ELECTRIC
    }

    public enum S {
        SWING, HEAVY_SWING, HIT_CRIT, BLADE_DRAW, SLAM, ROAR, THUNDER, FIRE_WHOOSH, FROST, MAGIC_CAST, HEAL,
        SHRINE_KINDLE, SHRINE_REST, LEVEL_UP, SMITH, BUY, BOSS_SPAWN, BOSS_DEATH, DODGE, BLEED, TELEPORT, RUNES_PICKUP,
        DRINK, EXPLODE, BEAM, DENY, CHIME, WITHER, GRAVITY, SHARD
    }

    public enum E {
        SLOWNESS, POISON, WITHER, REGENERATION, RESISTANCE, STRENGTH, SPEED, WEAKNESS, BLINDNESS, DARKNESS,
        FIRE_RESISTANCE, ABSORPTION, GLOWING, LEVITATION, SLOW_FALLING, NAUSEA, HASTE, MINING_FATIGUE
    }

    public enum A {
        MAX_HEALTH, ATTACK_DAMAGE, ATTACK_SPEED, KNOCKBACK_RESISTANCE, LUCK, MOVEMENT_SPEED, ARMOR, SCALE,
        ENTITY_REACH, FOLLOW_RANGE
    }

    public static void burst(ServerLevel level, P p, Vec3 at, int count, double spread, double speed) {
        Compat.particles(level, p, at.x, at.y, at.z, count, spread, spread, spread, speed);
    }

    public static void burst(ServerLevel level, P p, Entity e, int count, double speed) {
        Compat.particles(level, p, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), count,
                e.getBbWidth() * 0.4, e.getBbHeight() * 0.3, e.getBbWidth() * 0.4, speed);
    }

    /** Particles along a segment, one every {@code step} blocks. */
    public static void line(ServerLevel level, P p, Vec3 from, Vec3 to, double step) {
        Vec3 d = to.subtract(from);
        double len = d.length();
        if (len < 1.0E-4) {
            return;
        }
        Vec3 n = d.scale(1 / len);
        for (double t = 0; t <= len; t += step) {
            Vec3 at = from.add(n.scale(t));
            Compat.particles(level, p, at.x, at.y, at.z, 1, 0.02, 0.02, 0.02, 0);
        }
    }

    /** Horizontal ring of particles. */
    public static void ring(ServerLevel level, P p, Vec3 center, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double a = Math.PI * 2 * i / points;
            Compat.particles(level, p, center.x + Math.cos(a) * radius, center.y, center.z + Math.sin(a) * radius, 1,
                    0.05, 0.02, 0.05, 0.01);
        }
    }

    /** Vertical column of particles, e.g. falling stars or pillars. */
    public static void column(ServerLevel level, P p, Vec3 base, double height, int points, double spread) {
        for (int i = 0; i < points; i++) {
            double y = base.y + height * i / points;
            Compat.particles(level, p, base.x, y, base.z, 1, spread, 0.05, spread, 0.01);
        }
    }

    /** Rising double helix around a point; keeps particles out of the player's own view. */
    public static void helix(ServerLevel level, P p, Vec3 base, double radius, double height, int points) {
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;
            double a = t * Math.PI * 6;
            for (int strand = 0; strand < 2; strand++) {
                double ang = a + strand * Math.PI;
                Compat.particles(level, p, base.x + Math.cos(ang) * radius, base.y + t * height,
                        base.z + Math.sin(ang) * radius, 1, 0.01, 0.01, 0.01, 0.005);
            }
        }
    }

    /** Arc of particles in front of an entity, used for slashes. */
    public static void slash(ServerLevel level, P p, Entity e, double radius, double degrees, double heightOffset) {
        double yaw = Math.toRadians(e.getYRot() + 90);
        double half = Math.toRadians(degrees) / 2;
        int points = (int) Math.max(6, radius * degrees / 12);
        for (int i = 0; i <= points; i++) {
            double a = yaw - half + (2 * half) * i / points;
            double x = e.getX() + Math.cos(a) * radius;
            double z = e.getZ() + Math.sin(a) * radius;
            Compat.particles(level, p, x, e.getY() + e.getBbHeight() * 0.6 + heightOffset, z, 1, 0.02, 0.02, 0.02, 0);
        }
    }

    public static void sound(Entity at, S s, float volume, float pitch) {
        Compat.sound(at.level(), at.getX(), at.getY(), at.getZ(), s, volume, pitch);
    }

    private Fx() {
    }
}
