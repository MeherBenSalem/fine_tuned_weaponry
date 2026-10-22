package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.art.Spell;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.lang.Lang;
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
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;


/** Sorceries and incantations cast from staves and seals. */
public final class Spells {
    static boolean cast(ServerPlayer player, WeaponSpec spec, AttackRating ar, PlayerData d, Session s, long now) {
        Spell spell = spec.spell;
        if (d.effective(spell.school.attr) < spell.requirement) {
            Players.actionBar(player, Lang.MSG_SPELL_REQ);
            Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
            return false;
        }
        if (s.onCooldown("spell", now)) {
            return false;
        }
        double cost = spell.fp * (d.shard() == CrownShard.WYRM ? 0.8 : 1.0);
        if (!player.isCreative()) {
            if (d.focus() < cost) {
                Players.actionBar(player, Lang.MSG_NO_FP);
                Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
                return false;
            }
            d.setFocus(d.focus() - cost);
        }
        // Dexterity shortens casting recovery, up to a third.
        double haste = 1 - 0.33 * Stats.scalingCurve(d.effective(Attr.DEXTERITY));
        int cooldown = Math.max(6, (int) Math.round(spell.cooldown * haste));
        s.cooldown("spell", now, cooldown);
        Compat.itemCooldown(player, player.getMainHandItem(), cooldown);
        double power = ar.spellPower / 100.0;
        double damage = spell.base * power;
        perform(player, spell, damage, power, d);
        Players.syncVitals(player, true);
        return true;
    }

    private static void perform(ServerPlayer player, Spell spell, double damage, double power, PlayerData d) {
        ServerLevel level = (ServerLevel) player.level();
        Vec3 eye = player.getEyePosition();
        switch (spell.kind) {
            case BOLT -> bolt(player, damage, spell.range, Fx.P.END_ROD, Fx.P.MAGIC_CRIT, Combat.Kind.MAGIC, null, 0);
            case BARRAGE -> {
                for (int i = 0; i < 5; i++) {
                    RbServer.later(1 + i * 3, () -> {
                        if (player.isAlive()) {
                            bolt(player, damage, spell.range, Fx.P.END_ROD, Fx.P.MAGIC_CRIT, Combat.Kind.MAGIC, null, 0);
                        }
                    });
                }
            }
            case FROST_BURST -> {
                Vec3 at = Targets.aim(player, spell.range);
                Fx.line(level, Fx.P.SNOWFLAKE, eye, at, 0.4);
                Fx.sound(player, Fx.S.FROST, 1f, 0.8f);
                RbServer.later(3, () -> {
                    Fx.burst(level, Fx.P.SNOWFLAKE, at.add(0, 0.5, 0), 80, 1.6, 0.1);
                    Fx.burst(level, Fx.P.WHITE_ASH, at.add(0, 0.5, 0), 40, 1.2, 0.02);
                    for (LivingEntity e : Targets.around(player, at, 3)) {
                        Combat.hit(player, e, damage, Combat.Kind.MAGIC);
                        Status.add(e, StatusType.FROST, 60, player);
                    }
                });
            }
            case RAY -> {
                Vec3 end = Targets.point(player, spell.range);
                Fx.line(level, Fx.P.SONIC_BOOM, eye, end, 1.6);
                Fx.line(level, Fx.P.END_ROD, eye, end, 0.25);
                Fx.sound(player, Fx.S.BEAM, 1.4f, 0.6f);
                for (LivingEntity e : Targets.segment(player, eye, end, 0.9)) {
                    Combat.hit(player, e, damage, Combat.Kind.MAGIC);
                }
            }
            case CHAIN_LIGHTNING -> {
                LivingEntity first = Targets.ray(player, spell.range);
                Fx.sound(player, Fx.S.THUNDER, 0.8f, 1.5f);
                if (first == null) {
                    Fx.line(level, Fx.P.ELECTRIC, eye, Targets.point(player, spell.range), 0.25);
                    return;
                }
                Fx.line(level, Fx.P.ELECTRIC, eye, first.getEyePosition(), 0.2);
                Combat.hit(player, first, damage, Combat.Kind.LIGHTNING);
                LivingEntity prev = first;
                double dmg = damage;
                Set<Integer> hit = new HashSet<>();
                hit.add(first.getId());
                for (int i = 0; i < 2; i++) {
                    dmg *= 0.8;
                    LivingEntity next = null;
                    for (LivingEntity e : Targets.around(player, prev.position(), 6)) {
                        if (!hit.contains(e.getId())) {
                            next = e;
                            break;
                        }
                    }
                    if (next == null) {
                        break;
                    }
                    hit.add(next.getId());
                    Fx.line(level, Fx.P.ELECTRIC, prev.getEyePosition(), next.getEyePosition(), 0.2);
                    Combat.hit(player, next, dmg, Combat.Kind.LIGHTNING);
                    prev = next;
                }
            }
            case GRAVITY_BURST -> {
                Vec3 at = Targets.aim(player, spell.range).add(0, 1, 0);
                Fx.line(level, Fx.P.REVERSE_PORTAL, eye, at, 0.4);
                Fx.sound(player, Fx.S.GRAVITY, 1f, 0.7f);
                RbServer.later(4, () -> {
                    Fx.burst(level, Fx.P.REVERSE_PORTAL, at, 120, 2.0, 0.6);
                    for (LivingEntity e : Targets.around(player, at, 4)) {
                        Compat.push(e, at.subtract(e.position()).normalize().scale(0.8));
                        Combat.hit(player, e, damage, Combat.Kind.MAGIC);
                    }
                });
            }
            case FIRE_BOLT -> {
                LivingEntity t = bolt(player, damage, spell.range, Fx.P.FLAME, Fx.P.LAVA, Combat.Kind.FIRE, null, 0);
                if (t != null) {
                    Compat.ignite(t, 4);
                }
            }
            case HEAL -> {
                double bless = 1 + Stats.blessing(d.effective(Attr.FAITH));
                double fraction = Math.min(0.8, 0.2 * power) * bless;
                for (ServerPlayer ally : level.players()) {
                    if (ally.distanceToSqr(player) <= spell.range * spell.range && (ally == player || !Targets.hostile(player, ally))) {
                        ally.heal((float) (ally.getMaxHealth() * fraction));
                        Compat.addEffect(ally, Fx.E.REGENERATION, 80, 0);
                        Fx.helix(level, Fx.P.GLOW, ally.position(), 0.9, 2.2, 14);
                        Fx.burst(level, Fx.P.HAPPY, ally, 12, 0.05);
                    }
                }
                Fx.ring(level, Fx.P.END_ROD, player.position().add(0, 0.2, 0), spell.range * 0.6, 40);
                Fx.sound(player, Fx.S.HEAL, 1f, 1f);
            }
            case LIGHTNING_SPEAR -> {
                Vec3 at = Targets.aim(player, spell.range);
                Fx.line(level, Fx.P.ELECTRIC, eye, at, 0.2);
                Compat.lightning(level, at, true);
                for (LivingEntity e : Targets.around(player, at, 2.2)) {
                    Combat.hit(player, e, damage, Combat.Kind.LIGHTNING);
                }
            }
            case FLAME_PILLAR -> {
                Vec3 at = Targets.aim(player, spell.range);
                Fx.burst(level, Fx.P.SMOKE, at, 20, 0.6, 0.02);
                Fx.sound(player, Fx.S.MAGIC_CAST, 1f, 0.6f);
                RbServer.later(10, () -> {
                    Fx.column(level, Fx.P.FLAME, at, 6, 60, 0.6);
                    Fx.column(level, Fx.P.LAVA, at, 3, 10, 0.5);
                    Compat.sound(level, at.x, at.y, at.z, Fx.S.FIRE_WHOOSH, 1.4f, 0.7f);
                    for (LivingEntity e : Targets.around(player, at.add(0, 1, 0), 2.5)) {
                        Combat.hit(player, e, damage, Combat.Kind.FIRE);
                        Compat.ignite(e, 6);
                        Compat.push(e, new Vec3(0, 0.7, 0));
                    }
                });
            }
            case BLOOD_BURST -> {
                float self = Math.max(1, player.getHealth() * 0.05f);
                if (player.getHealth() > self + 1) {
                    Combat.pure(player, self, null);
                }
                Compat.dust(level, 0xB3262B, 1.8f, player.getX(), player.getY() + 1, player.getZ(), 40, 0.8);
                Fx.sound(player, Fx.S.BLEED, 1f, 0.7f);
                for (LivingEntity e : Targets.cone(player, spell.range, 120)) {
                    Combat.hit(player, e, damage, Combat.Kind.PHYSICAL);
                    Status.add(e, StatusType.BLEED, 60 * Stats.potency(d.effective(Attr.ARCANE)), player);
                    Compat.dust(level, 0xB3262B, 1.4f, e.getX(), e.getY() + e.getBbHeight() * 0.6, e.getZ(), 20, 0.4);
                }
            }
            case BREATH -> Arts.repeat(6, 5, i -> {
                Fx.sound(player, Fx.S.FIRE_WHOOSH, 0.8f, 0.7f + i * 0.05f);
                Vec3 look = player.getLookAngle();
                for (double r = 1; r <= spell.range; r += 1) {
                    Vec3 p = player.getEyePosition().add(look.scale(r));
                    Fx.burst(level, i % 2 == 0 ? Fx.P.FLAME : Fx.P.DRAGON_BREATH, p, 3, r * 0.12, 0.02);
                }
                for (LivingEntity e : Targets.cone(player, spell.range, 60)) {
                    Combat.hit(player, e, damage, Combat.Kind.FIRE);
                    Compat.ignite(e, 4);
                }
            }, player);
        }
    }

    private static LivingEntity bolt(ServerPlayer player, double damage, double range, Fx.P trail, Fx.P impact,
                                     Combat.Kind kind, StatusType status, double buildup) {
        ServerLevel level = (ServerLevel) player.level();
        Vec3 eye = player.getEyePosition();
        LivingEntity target = Targets.ray(player, range);
        Vec3 end = target != null ? target.getEyePosition() : Targets.point(player, range);
        Fx.line(level, trail, eye.add(player.getLookAngle().scale(0.6)), end, 0.3);
        Fx.sound(player, Fx.S.MAGIC_CAST, 0.8f, 1.3f);
        Fx.burst(level, impact, end, 10, 0.2, 0.1);
        if (target != null) {
            Combat.hit(player, target, damage, kind);
            if (status != null) {
                Status.add(target, status, buildup, player);
            }
        }
        return target;
    }

    private Spells() {
    }
}
