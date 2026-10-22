package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.art.Art;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import io.nightbeam.runeborn.core.weapon.AttackRating;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Weapon arts. Entry point: {@link #use}, called when a Runeborn weapon is used. */
public final class Arts {
    /** Returns true if the art (or spell) fired. */
    public static boolean use(ServerPlayer player, ItemStack stack) {
        WeaponSpec spec = RbItems.weapon(stack);
        if (spec == null || !RbServer.running()) {
            return false;
        }
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        long now = RbServer.now();
        int[] attrs = d.effectiveAttrs();
        AttackRating ar = AttackRating.of(spec, Stacks.upgrade(stack), Stacks.affinity(stack), attrs);
        if (ar.anyUnmet) {
            Players.actionBar(player, Lang.MSG_REQUIREMENT);
            Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
            return false;
        }
        if (spec.art == Art.SPELLCAST) {
            return Spells.cast(player, spec, ar, d, s, now);
        }
        Art art = spec.art;
        String key = "art";
        if (s.onCooldown(key, now)) {
            Players.actionBar(player, Lang.MSG_COOLDOWN);
            return false;
        }
        if (!player.isCreative()) {
            if (d.focus() < art.fp) {
                Players.actionBar(player, Lang.MSG_NO_FP);
                Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
                return false;
            }
            if (ServerConfig.INSTANCE.staminaEnabled && s.stamina(d.maxStamina()) < Math.min(art.stamina, d.maxStamina() * 0.5)) {
                Players.actionBar(player, Lang.MSG_NO_STAMINA);
                return false;
            }
            d.setFocus(d.focus() - art.fp);
            s.stamina = Math.max(0, s.stamina(d.maxStamina()) - art.stamina);
            s.staminaRegenAt = now + 20;
        }
        s.cooldown(key, now, art.cooldown);
        Compat.itemCooldown(player, stack, art.cooldown);
        perform(player, art, ar, s, now);
        Players.syncVitals(player, true);
        return true;
    }

    private static void perform(ServerPlayer player, Art art, AttackRating ar, Session s, long now) {
        ServerLevel level = (ServerLevel) player.level();
        double mult = art.mult;
        switch (art.kind) {
            case DASH -> {
                Vec3 dir = Targets.flatLook(player);
                Compat.push(player, dir.scale(1.25).add(0, 0.12, 0));
                s.invulnerableUntil = now + 7;
                Fx.burst(level, Fx.P.CLOUD, player, 12, 0.02);
                Fx.sound(player, Fx.S.DODGE, 0.8f, 1.2f);
            }
            case CONE_STRIKE -> {
                Compat.addEffect(player, Fx.E.SLOWNESS, 8, 3);
                Fx.sound(player, Fx.S.BLADE_DRAW, 1f, 0.8f);
                RbServer.later(7, () -> {
                    if (!player.isAlive()) {
                        return;
                    }
                    Fx.slash(level, Fx.P.SWEEP, player, art.range * 0.7, 100, 0);
                    Fx.slash(level, Fx.P.END_ROD, player, art.range * 0.9, 100, 0);
                    Fx.sound(player, Fx.S.HEAVY_SWING, 1f, 1.4f);
                    for (LivingEntity e : Targets.cone(player, art.range, 100)) {
                        Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 1.5);
                    }
                });
            }
            case FLURRY -> repeat(3, 4, i -> {
                Fx.slash(level, Fx.P.DAMAGE, player, art.range * 0.7, 90, (i - 1) * 0.3);
                Compat.dust(level, 0xB3262B, 1.2f, player.getX() + Targets.flatLook(player).x * 1.6, player.getY() + 1.1,
                        player.getZ() + Targets.flatLook(player).z * 1.6, 12, 0.6);
                Fx.sound(player, Fx.S.SWING, 1f, 1.3f + i * 0.1f);
                for (LivingEntity e : Targets.cone(player, art.range, 90)) {
                    Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 0.6);
                    Status.add(e, StatusType.BLEED, 25, player);
                }
            }, player);
            case BUFF_ATTACK -> {
                s.warCryUntil = now + 400;
                Compat.addEffect(player, Fx.E.RESISTANCE, 400, 0);
                Fx.ring(level, Fx.P.CRIT, player.position().add(0, 0.2, 0), 1.5, 24);
                Fx.burst(level, Fx.P.CRIT, player, 14, 0.25);
                Fx.sound(player, Fx.S.ROAR, 1f, 1.2f);
            }
            case SLAM -> slam(player, ar, mult, art.range);
            case LEAP_SLAM -> {
                Vec3 dir = Targets.flatLook(player);
                Compat.push(player, dir.scale(0.9).add(0, 0.75, 0));
                Fx.sound(player, Fx.S.HEAVY_SWING, 1f, 0.7f);
                landing(player, ar, mult, art.range, 0);
            }
            case LINE -> {
                Compat.push(player, Targets.flatLook(player).scale(0.6));
                Fx.line(level, Fx.P.CRIT, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(art.range)), 0.4);
                Fx.sound(player, Fx.S.SWING, 1f, 0.9f);
                for (LivingEntity e : Targets.line(player, art.range, 0.6)) {
                    Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 1.2);
                }
            }
            case SPIN -> spin(player, ar, mult, art.range, 2.0, Fx.P.SWEEP);
            case MULTI_SPIN -> repeat(3, 6, i -> spin(player, ar, mult, art.range, 0.7, Fx.P.SWEEP), player);
            case VOLLEY -> {
                List<LivingEntity> targets = Targets.nearest(player, art.range, 3, 140);
                Fx.sound(player, Fx.S.MAGIC_CAST, 1f, 1.4f);
                int i = 0;
                for (LivingEntity e : targets) {
                    int delay = 2 + i * 3;
                    RbServer.later(delay, () -> {
                        if (!e.isAlive()) {
                            return;
                        }
                        Fx.line(level, Fx.P.END_ROD, player.getEyePosition(), e.getEyePosition(), 0.35);
                        Fx.burst(level, Fx.P.MAGIC_CRIT, e, 12, 0.2);
                        Combat.artHit(player, e, ar, mult, Combat.Kind.MAGIC, 0.5);
                    });
                    i++;
                }
                if (targets.isEmpty()) {
                    Fx.line(level, Fx.P.END_ROD, player.getEyePosition(), Targets.point(player, art.range), 0.35);
                }
            }
            case STORM -> {
                List<LivingEntity> targets = Targets.nearest(player, art.range, 4, 360);
                for (LivingEntity e : targets) {
                    Compat.lightning(level, e.position(), true);
                    Combat.artHit(player, e, ar, mult, Combat.Kind.LIGHTNING, 0.8);
                }
                Fx.sound(player, Fx.S.THUNDER, 1f, 1f);
                Fx.burst(level, Fx.P.ELECTRIC, player, 30, 0.3);
            }
            case FROST_WAVE -> wave(player, ar, mult, art.range, 70, Fx.P.SNOWFLAKE, Combat.Kind.MAGIC, StatusType.FROST, 70);
            case FLAME_WAVE -> {
                for (LivingEntity e : wave(player, ar, mult, art.range, 90, Fx.P.FLAME, Combat.Kind.FIRE, null, 0)) {
                    Compat.ignite(e, 5);
                }
            }
            case BUFF_HOLY -> {
                s.holyBuffUntil = now + 600;
                Fx.helix(level, Fx.P.GLOW, player.position(), 0.9, 2.2, 18);
                Fx.burst(level, Fx.P.END_ROD, player, 20, 0.05);
                Fx.sound(player, Fx.S.HEAL, 1f, 1.5f);
            }
            case PULL -> {
                Fx.burst(level, Fx.P.REVERSE_PORTAL, player.position().add(0, 1, 0), 80, art.range * 0.4, 0.4);
                Fx.sound(player, Fx.S.GRAVITY, 1f, 0.6f);
                for (LivingEntity e : Targets.around(player, player.position(), art.range)) {
                    Vec3 pull = player.position().subtract(e.position()).normalize().scale(1.1).add(0, 0.2, 0);
                    Compat.push(e, pull);
                    Combat.artHit(player, e, ar, mult, Combat.Kind.MAGIC, 0.5);
                }
            }
            case WAVE -> {
                Vec3 from = player.position().add(0, 1, 0);
                Vec3 to = from.add(Targets.flatLook(player).scale(art.range));
                Fx.line(level, Fx.P.SOUL_FIRE, from, to, 0.25);
                Fx.line(level, Fx.P.END_ROD, from.add(0, 0.5, 0), to.add(0, 0.5, 0), 0.5);
                Fx.sound(player, Fx.S.BEAM, 1f, 1.3f);
                for (LivingEntity e : Targets.segment(player, from, to, 1.2)) {
                    Combat.artHit(player, e, ar, mult, Combat.Kind.MAGIC, 1.0);
                }
            }
            case METEOR -> {
                Vec3 at = Targets.aim(player, 24);
                Fx.sound(player, Fx.S.MAGIC_CAST, 1f, 0.5f);
                for (int t = 0; t < 14; t += 2) {
                    double h = 14 - t;
                    RbServer.later(t + 1, () -> Fx.burst(level, Fx.P.FLAME, at.add(0, h, 0), 12, 0.3, 0.02));
                }
                RbServer.later(16, () -> {
                    Fx.burst(level, Fx.P.EXPLOSION_BIG, at, 1, 0, 0);
                    Fx.burst(level, Fx.P.LAVA, at, 30, 1.5, 0.2);
                    Compat.sound(level, at.x, at.y, at.z, Fx.S.EXPLODE, 1.4f, 0.8f);
                    for (LivingEntity e : Targets.around(player, at, art.range)) {
                        Combat.artHit(player, e, ar, mult, Combat.Kind.FIRE, 1.0);
                        Compat.ignite(e, 5);
                        Compat.push(e, e.position().subtract(at).normalize().scale(0.8).add(0, 0.5, 0));
                    }
                });
            }
            case HEAL_AURA -> {
                double bless = 1 + Stats.blessing(RbServer.data(player).effective(Attr.FAITH));
                for (ServerPlayer ally : level.players()) {
                    if (ally.distanceToSqr(player) <= art.range * art.range && (ally == player || !Targets.hostile(player, ally))) {
                        ally.heal((float) (ally.getMaxHealth() * 0.3 * bless));
                        Compat.addEffect(ally, Fx.E.REGENERATION, 100, 1);
                        Fx.burst(level, Fx.P.CHERRY, ally, 30, 0.05);
                    }
                }
                Fx.ring(level, Fx.P.CHERRY, player.position().add(0, 0.3, 0), art.range * 0.6, 40);
                Fx.sound(player, Fx.S.HEAL, 1f, 1.1f);
            }
            case DRAIN_SPIN -> {
                List<LivingEntity> hit = spin(player, ar, mult, art.range, 1.0, Fx.P.SOUL);
                if (!hit.isEmpty()) {
                    player.heal((float) (ar.total * mult * hit.size() * 0.25));
                    Fx.burst(level, Fx.P.SCULK_SOUL, player, 12, 0.05);
                }
            }
            case THUNDER_DASH -> {
                Vec3 start = player.position();
                Vec3 dir = Targets.flatLook(player);
                Compat.push(player, dir.scale(2.2).add(0, 0.1, 0));
                s.invulnerableUntil = now + 10;
                Fx.sound(player, Fx.S.THUNDER, 0.8f, 1.6f);
                RbServer.later(4, () -> {
                    Vec3 end = player.position();
                    Fx.line(level, Fx.P.ELECTRIC, start.add(0, 1, 0), end.add(0, 1, 0), 0.2);
                    for (LivingEntity e : Targets.segment(player, start.add(0, 1, 0), end.add(dir.scale(1.5)).add(0, 1, 0), 1.2)) {
                        Combat.artHit(player, e, ar, mult, Combat.Kind.LIGHTNING, 0.8);
                        Fx.burst(level, Fx.P.ELECTRIC, e, 10, 0.3);
                    }
                });
            }
            case BLINK_STRIKE -> {
                LivingEntity target = Targets.ray(player, art.range);
                if (target == null) {
                    Fx.burst(level, Fx.P.SMOKE, player, 10, 0.05);
                    return;
                }
                Vec3 behind = target.position().subtract(Targets.flatLook(target).scale(1.4));
                Fx.burst(level, Fx.P.LARGE_SMOKE, player, 20, 0.05);
                player.teleportTo(behind.x, target.getY(), behind.z);
                Compat.face(player, target);
                s.invulnerableUntil = now + 5;
                Fx.sound(player, Fx.S.TELEPORT, 0.8f, 1.4f);
                Fx.burst(level, Fx.P.LARGE_SMOKE, player, 20, 0.05);
                Combat.artHit(player, target, ar, mult, Combat.Kind.PHYSICAL, 1.5);
            }
            case FLURRY5 -> repeat(5, 3, i -> {
                Fx.slash(level, Fx.P.SWEEP, player, art.range * 0.7, 80, (i % 2) * 0.3);
                Fx.sound(player, Fx.S.SWING, 0.9f, 0.9f + i * 0.08f);
                for (LivingEntity e : Targets.cone(player, art.range, 90)) {
                    Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 0.4);
                }
            }, player);
            case CHARGE -> {
                Set<Integer> hit = new HashSet<>();
                Vec3 dir = Targets.flatLook(player);
                Fx.sound(player, Fx.S.ROAR, 0.7f, 0.8f);
                repeat(8, 1, i -> {
                    Compat.push(player, dir.scale(0.9));
                    Fx.burst(level, Fx.P.CLOUD, player.position(), 3, 0.2, 0.01);
                    for (LivingEntity e : Targets.around(player, player.position().add(dir.scale(1.2)), 1.6)) {
                        if (hit.add(e.getId())) {
                            Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 1.0);
                            Compat.push(e, dir.scale(1.2).add(0, 0.4, 0));
                            Fx.sound(e, Fx.S.SLAM, 0.8f, 1.2f);
                        }
                    }
                }, player);
            }
            case CAST -> {
            }
        }
        Players.send(player, Packets.fx(Packets.Fx.SHAKE, 0.4f));
    }

    private static void slam(ServerPlayer player, AttackRating ar, double mult, double radius) {
        ServerLevel level = (ServerLevel) player.level();
        Vec3 at = player.position();
        Fx.ring(level, Fx.P.EXPLOSION, at.add(0, 0.2, 0), radius * 0.8, 10);
        Fx.ring(level, Fx.P.CLOUD, at.add(0, 0.1, 0), radius, 30);
        Compat.blockBurst(level, at, radius, 40);
        Fx.sound(player, Fx.S.SLAM, 1.2f, 0.7f);
        for (LivingEntity e : Targets.around(player, at, radius)) {
            Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, 1.0);
            Compat.push(e, e.position().subtract(at).normalize().scale(0.5).add(0, 0.65, 0));
        }
        Players.send(player, Packets.fx(Packets.Fx.SHAKE, 0.8f));
    }

    /** Polls until the player lands after a leap, then slams. */
    private static void landing(ServerPlayer player, AttackRating ar, double mult, double radius, int waited) {
        RbServer.later(1, () -> {
            if (!player.isAlive()) {
                return;
            }
            player.resetFallDistance();
            if ((player.onGround() && waited >= 3) || waited > 40) {
                slam(player, ar, mult, radius);
            } else {
                landing(player, ar, mult, radius, waited + 1);
            }
        });
    }

    private static List<LivingEntity> spin(ServerPlayer player, AttackRating ar, double mult, double radius,
                                           double statusMult, Fx.P particle) {
        ServerLevel level = (ServerLevel) player.level();
        Fx.ring(level, particle, player.position().add(0, 1.0, 0), radius * 0.8, 20);
        Fx.sound(player, Fx.S.HEAVY_SWING, 1f, 1.1f);
        List<LivingEntity> targets = Targets.around(player, player.position().add(0, 1, 0), radius);
        for (LivingEntity e : targets) {
            Combat.artHit(player, e, ar, mult, Combat.Kind.PHYSICAL, statusMult);
        }
        return targets;
    }

    private static List<LivingEntity> wave(ServerPlayer player, AttackRating ar, double mult, double range, double degrees,
                                           Fx.P particle, Combat.Kind kind, StatusType status, double buildup) {
        ServerLevel level = (ServerLevel) player.level();
        for (double r = 1; r <= range; r += 1) {
            final double radius = r;
            RbServer.later((int) r, () -> Fx.slash(level, particle, player, radius, degrees, -0.8));
        }
        Fx.sound(player, kind == Combat.Kind.FIRE ? Fx.S.FIRE_WHOOSH : Fx.S.FROST, 1f, 1f);
        List<LivingEntity> targets = Targets.cone(player, range, degrees);
        for (LivingEntity e : targets) {
            Combat.artHit(player, e, ar, mult, kind, 1.0);
            if (status != null) {
                Status.add(e, status, buildup, player);
            }
        }
        return targets;
    }

    interface Step {
        void run(int index);
    }

    /** Runs {@code step} {@code times} times, {@code gap} ticks apart, while {@code owner} lives. */
    static void repeat(int times, int gap, Step step, LivingEntity owner) {
        for (int i = 0; i < times; i++) {
            final int index = i;
            if (i == 0) {
                step.run(0);
            } else {
                RbServer.later(i * gap, () -> {
                    if (owner.isAlive()) {
                        step.run(index);
                    }
                });
            }
        }
    }

    private Arts() {
    }
}
