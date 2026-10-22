package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.EliteAffix;
import io.nightbeam.runeborn.core.mob.MobLevels;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import io.nightbeam.runeborn.core.weapon.AttackRating;
import io.nightbeam.runeborn.core.weapon.DamageType;
import io.nightbeam.runeborn.core.weapon.StatusType;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * Damage pipeline. {@link #incoming} runs before vanilla damage math (scaling, i-frames,
 * stamina); {@link #afterDamage} runs once the hit landed (status buildup, on-hit effects, HUD).
 */
public final class Combat {
    /** Weapon arts, spells and status procs set this so their damage is not scaled twice. */
    private static int bypass;
    private static Melee pending;
    private static final Random RANDOM = new Random();

    private record Melee(ServerPlayer player, LivingEntity target, WeaponSpec spec, AttackRating ar, boolean full,
                         double statusMult) {
    }

    public enum Kind {
        PHYSICAL, MAGIC, FIRE, LIGHTNING, HOLY
    }

    /** Returns the new damage amount, or a negative value to cancel the hit. */
    public static float incoming(LivingEntity target, DamageSource source, float amount) {
        if (!RbServer.running() || !(target.level() instanceof ServerLevel)) {
            return amount;
        }
        ServerConfig cfg = ServerConfig.INSTANCE;
        long now = RbServer.now();

        if (target instanceof ServerPlayer tp) {
            Session ts = RbServer.session(tp);
            if (ts.invulnerableUntil > now && !Compat.bypassesInvulnerability(source)) {
                return -1;
            }
            PlayerData td = RbServer.data(tp);
            CrownShard shard = td.shard();
            if (shard == CrownShard.ASH && Compat.isFire(source)) {
                return -1;
            }
            if (Compat.isMagic(source)) {
                amount *= (float) (1 - Stats.warding(td.effective(Attr.INTELLIGENCE)));
            }
        }
        if (Status.frostbitten(target, now)) {
            amount *= 1.2f;
        }

        if (bypass > 0) {
            return amount;
        }
        Entity attacker = source.getEntity();
        Entity direct = source.getDirectEntity();

        if (attacker instanceof ServerPlayer player && direct == player) {
            amount = melee(player, target, amount, cfg, now);
        } else if (attacker instanceof Mob mob && attacker != target) {
            int level = Mobs.level(mob);
            if (direct != attacker && level > 1 && cfg.mobLevelsEnabled) {
                // Projectiles ignore the attack damage attribute, so scale them here.
                amount *= (float) MobLevels.damageMultiplier(cfg, level);
            }
            if (Mobs.isElite(mob)) {
                eliteOnHit(mob, target);
            }
        }
        if (target instanceof ServerPlayer && attacker instanceof ServerPlayer && attacker != target) {
            amount *= (float) cfg.pvpDamageMultiplier;
        }
        return amount;
    }

    private static float melee(ServerPlayer player, LivingEntity target, float amount, ServerConfig cfg, long now) {
        ItemStack held = player.getMainHandItem();
        WeaponSpec spec = Stacks.profile(held);
        if (spec == null || (!cfg.vanillaWeaponsScale && RbItems.weapon(held) == null)) {
            return amount;
        }
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        if (s.lastMeleeAt == now && s.lastMeleeTarget != target.getId()) {
            // A vanilla sweep follow-up from the same swing: Runeborn sweeps by weapon class instead.
            return -1;
        }
        s.lastMeleeAt = now;
        s.lastMeleeTarget = target.getId();
        int[] attrs = d.effectiveAttrs();
        AttackRating ar = AttackRating.of(spec, Stacks.upgrade(held), Stacks.affinity(held), attrs);
        double base = spec.baseAttack();
        boolean full = amount >= base * 0.85f;
        double mult = ar.total / Math.max(0.1, base);
        mult *= offensiveBonus(player, d, s, target, ar, now);

        if (ar.anyUnmet && !s.onCooldown("warn_req", now)) {
            s.cooldown("warn_req", now, 60);
            Players.actionBar(player, Lang.MSG_REQUIREMENT);
        }
        if (cfg.staminaEnabled && cfg.attacksCostStamina && !player.isCreative()) {
            double st = s.stamina(d.maxStamina());
            int cost = spec.cls.stamina;
            if (st < cost) {
                mult *= cfg.exhaustedDamageMultiplier;
            }
            s.stamina = Math.max(0, st - cost);
            s.staminaRegenAt = now + 16;
        }
        pending = new Melee(player, target, spec, ar, full, 1.0);
        return (float) (amount * mult);
    }

    /** Damage bonuses from buffs, crown shards and holy-vs-undead. */
    static double offensiveBonus(ServerPlayer player, PlayerData d, Session s, LivingEntity target, AttackRating ar, long now) {
        double mult = 1;
        if (s.warCryUntil > now) {
            mult *= 1.2;
        }
        if (s.holyBuffUntil > now) {
            mult *= 1.25;
            if (Compat.isUndead(target)) {
                mult *= 1.15;
            }
        }
        double holy = ar.share(DamageType.HOLY);
        if (holy > 0 && Compat.isUndead(target)) {
            mult *= 1 + 0.35 * holy;
        }
        CrownShard shard = d.shard();
        if (shard == CrownShard.ASH) {
            mult *= 1 + 0.15 * ar.share(DamageType.PHYSICAL);
        } else if (shard == CrownShard.STORM) {
            mult *= 1 + 0.2 * ar.share(DamageType.LIGHTNING);
        } else if (shard == CrownShard.END) {
            mult *= 1.1;
        }
        return mult;
    }

    /** Called after a hit lands. {@code dealt} is the health actually removed. */
    public static void afterDamage(LivingEntity target, DamageSource source, float dealt) {
        if (!RbServer.running() || !(target.level() instanceof ServerLevel level)) {
            return;
        }
        Entity attacker = source.getEntity();
        Melee m = pending;
        pending = null;
        if (m != null && m.target == target && bypass == 0) {
            onMeleeHit(m, level);
        }
        if (attacker instanceof ServerPlayer player && attacker != target) {
            BossManager.onDamaged(target, player);
            sendTarget(player, target, dealt);
        }
        if (target instanceof ServerPlayer tp && attacker instanceof LivingEntity) {
            RbServer.session(tp).staminaRegenAt = Math.max(RbServer.session(tp).staminaRegenAt, RbServer.now() + 10);
        }
    }

    private static void onMeleeHit(Melee m, ServerLevel level) {
        applyStatus(m.player, m.target, m.ar, m.statusMult);
        double fire = m.ar.share(DamageType.FIRE);
        if (fire > 0.2) {
            Compat.ignite(m.target, 3);
        }
        double lightning = m.ar.share(DamageType.LIGHTNING);
        if (lightning > 0.2 && RANDOM.nextDouble() < 0.18) {
            arc(m.player, m.target, m.ar.total * 0.35);
        }
        if (m.full && m.spec.cls.sweeps()) {
            sweep(m.player, m.target, m.ar.total * 0.35);
        }
    }

    public static void applyStatus(ServerPlayer player, LivingEntity target, AttackRating ar, double mult) {
        double potency = Stats.potency(RbServer.data(player).effective(Attr.ARCANE));
        for (StatusType t : StatusType.ALL) {
            int v = ar.status[t.ordinal()];
            if (v > 0) {
                Status.add(target, t, v * potency * mult, player);
            }
        }
    }

    private static void sweep(ServerPlayer player, LivingEntity main, double damage) {
        ServerLevel level = (ServerLevel) player.level();
        Fx.slash(level, Fx.P.SWEEP, player, 2.2, 110, 0);
        for (LivingEntity e : Targets.around(player, main.position(), 1.6)) {
            if (e != main && e.distanceToSqr(player) < 16) {
                hit(player, e, damage, Kind.PHYSICAL);
            }
        }
    }

    private static void arc(ServerPlayer player, LivingEntity from, double damage) {
        ServerLevel level = (ServerLevel) player.level();
        for (LivingEntity e : Targets.around(player, from.position(), 5)) {
            if (e != from) {
                Fx.line(level, Fx.P.ELECTRIC, from.getEyePosition(), e.getEyePosition(), 0.3);
                Fx.sound(e, Fx.S.THUNDER, 0.4f, 1.8f);
                hit(player, e, damage, Kind.LIGHTNING);
                return;
            }
        }
    }

    /**
     * Deals already-scaled damage from {@code attacker} to {@code target}, bypassing the melee
     * scaling path. Returns true if the hit landed.
     */
    public static boolean hit(LivingEntity attacker, LivingEntity target, double amount, Kind kind) {
        if (amount <= 0 || !target.isAlive()) {
            return false;
        }
        bypass++;
        try {
            DamageSource src = kind == Kind.PHYSICAL ? Compat.attackSource(attacker) : Compat.magicSource(attacker);
            return Compat.hurt(target, src, (float) amount);
        } finally {
            bypass--;
        }
    }

    /** Hit plus status buildup from the attacker's weapon, for weapon arts. */
    public static boolean artHit(ServerPlayer player, LivingEntity target, AttackRating ar, double mult, Kind kind,
                                 double statusMult) {
        PlayerData d = RbServer.data(player);
        double bonus = offensiveBonus(player, d, RbServer.session(player), target, ar, RbServer.now());
        boolean landed = hit(player, target, ar.total * mult * bonus, kind);
        if (landed) {
            applyStatus(player, target, ar, statusMult);
            if (ar.share(DamageType.FIRE) > 0.2) {
                Compat.ignite(target, 3);
            }
        }
        return landed;
    }

    /** Damage that is not attributed to anyone (status procs fall back to kill credit). */
    public static void pure(LivingEntity target, double amount, LivingEntity credit) {
        if (amount <= 0 || !target.isAlive()) {
            return;
        }
        bypass++;
        try {
            Compat.hurt(target, credit != null ? Compat.magicSource(credit) : Compat.genericMagic(target), (float) amount);
        } finally {
            bypass--;
        }
    }

    private static void eliteOnHit(Mob elite, LivingEntity target) {
        ServerLevel level = (ServerLevel) elite.level();
        for (EliteAffix affix : Mobs.affixes(elite)) {
            switch (affix) {
                case STORMTOUCHED -> {
                    if (RANDOM.nextDouble() < 0.2) {
                        Compat.lightning(level, target.position(), true);
                        RbServer.later(1, () -> pure(target, 2 + Mobs.level(elite) * 0.08, elite));
                    }
                }
                case FROSTBORNE -> Status.add(target, StatusType.FROST, 30, elite);
                case VENOMOUS -> Compat.addEffect(target, Fx.E.POISON, 80, 0);
                case BLOODTHIRSTY -> {
                    Status.add(target, StatusType.BLEED, 25, elite);
                    elite.heal(1.5f);
                }
                case BLAZING -> Compat.ignite(target, 4);
                case PHASING -> {
                    if (RANDOM.nextDouble() < 0.15) {
                        Vec3 behind = target.position().subtract(Targets.flatLook(target).scale(1.5));
                        Fx.burst(level, Fx.P.PORTAL, elite, 20, 0.2);
                        elite.teleportTo(behind.x, behind.y, behind.z);
                    }
                }
                default -> {
                }
            }
        }
    }

    static void sendTarget(ServerPlayer player, LivingEntity target, float dealt) {
        Session s = RbServer.session(player);
        s.lastTargetId = target.getId();
        s.lastTargetAt = RbServer.now();
        s.lastHitDamage = dealt;
        Players.send(player, Packets.target(targetInfo(target, dealt)));
    }

    static Packets.Target targetInfo(LivingEntity target, float dealt) {
        int level = target instanceof ServerPlayer p ? RbServer.data(p).level() : Mobs.level(target);
        boolean elite = Mobs.isElite(target) || BossManager.isBoss(target);
        String name = target.getName().getString();
        return new Packets.Target(target.getId(), name, Math.max(1, level), target.getHealth(), target.getMaxHealth(),
                dealt, elite, 0, Status.meters(target));
    }

    public static boolean bypassing() {
        return bypass > 0;
    }

    private Combat() {
    }
}
