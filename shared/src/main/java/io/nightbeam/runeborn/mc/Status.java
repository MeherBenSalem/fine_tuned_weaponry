package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import io.nightbeam.runeborn.core.weapon.StatusType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Status buildup (bleed, frost, poison, blight): meters fill on hit, decay over time, burst when full. */
public final class Status {
    private static final Map<Integer, Meter> METERS = new HashMap<>();
    private static final Map<Integer, Long> FROSTBITTEN = new HashMap<>();

    private static final class Meter {
        final float[] value = new float[StatusType.ALL.length];
        long lastHit;
        int levelHint;
    }

    public static void add(LivingEntity target, StatusType type, double amount, LivingEntity source) {
        if (amount <= 0 || !target.isAlive()) {
            return;
        }
        if (target instanceof ServerPlayer p && type == StatusType.FROST && RbServer.data(p).shard() == CrownShard.FROST) {
            return;
        }
        Meter m = METERS.computeIfAbsent(target.getId(), id -> new Meter());
        m.lastHit = RbServer.now();
        float threshold = (float) threshold(target, type);
        float v = m.value[type.ordinal()] + (float) amount;
        if (v >= threshold) {
            m.value[type.ordinal()] = 0;
            proc(target, type, source);
        } else {
            m.value[type.ordinal()] = v;
        }
    }

    static double threshold(LivingEntity target, StatusType type) {
        if (target instanceof ServerPlayer p) {
            PlayerData d = RbServer.data(p);
            int stat = type == StatusType.BLEED || type == StatusType.FROST ? d.effective(Attr.ENDURANCE) : d.effective(Attr.VIGOR);
            return Stats.robustness(stat);
        }
        double t = 100 * (1 + Mobs.level(target) * 0.02);
        if (BossManager.isBoss(target)) {
            t *= 3;
        } else if (Mobs.isElite(target)) {
            t *= 1.5;
        }
        return t;
    }

    private static void proc(LivingEntity target, StatusType type, LivingEntity source) {
        ServerLevel level = (ServerLevel) target.level();
        boolean boss = BossManager.isBoss(target);
        float max = target.getMaxHealth();
        switch (type) {
            case BLEED -> {
                double dmg = max * (boss ? 0.045 : 0.10) + 3;
                Compat.dust(level, 0xB3262B, 1.6f, target.getX(), target.getY() + target.getBbHeight() * 0.6, target.getZ(),
                        40, target.getBbWidth() * 0.5);
                Fx.burst(level, Fx.P.DAMAGE, target, 8, 0.2);
                Fx.sound(target, Fx.S.BLEED, 1.0f, 0.9f);
                Combat.pure(target, dmg, source);
                if (source instanceof ServerPlayer p && RbServer.data(p).shard() == CrownShard.BLOOM) {
                    p.heal(p.getMaxHealth() * 0.02f);
                }
            }
            case FROST -> {
                double dmg = max * (boss ? 0.035 : 0.07) + 2;
                Fx.burst(level, Fx.P.SNOWFLAKE, target, 40, 0.15);
                Fx.burst(level, Fx.P.WHITE_ASH, target, 20, 0.05);
                Fx.sound(target, Fx.S.FROST, 1.0f, 1.2f);
                Combat.pure(target, dmg, source);
                Compat.addEffect(target, Fx.E.SLOWNESS, 100, 1);
                FROSTBITTEN.put(target.getId(), RbServer.now() + 120);
            }
            case POISON -> {
                Compat.addEffect(target, Fx.E.POISON, 160, boss ? 1 : 2);
                Fx.burst(level, Fx.P.WITCH, target, 20, 0.1);
                Fx.sound(target, Fx.S.WITHER, 0.4f, 1.6f);
            }
            case BLIGHT -> {
                Compat.addEffect(target, Fx.E.WITHER, 120, boss ? 1 : 2);
                Compat.dust(level, 0xC2603A, 1.2f, target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        30, target.getBbWidth() * 0.5);
                Fx.sound(target, Fx.S.WITHER, 0.6f, 0.8f);
            }
        }
    }

    public static boolean frostbitten(LivingEntity target, long now) {
        Long until = FROSTBITTEN.get(target.getId());
        return until != null && until > now;
    }

    /** Meter fill fractions for the HUD target bar. */
    public static float[] meters(LivingEntity target) {
        float[] out = new float[StatusType.ALL.length];
        Meter m = METERS.get(target.getId());
        if (m == null) {
            return out;
        }
        for (StatusType t : StatusType.ALL) {
            out[t.ordinal()] = (float) Math.min(1, m.value[t.ordinal()] / threshold(target, t));
        }
        return out;
    }

    /** Decay meters; called once a second. */
    public static void tick(long now) {
        Iterator<Map.Entry<Integer, Meter>> it = METERS.entrySet().iterator();
        while (it.hasNext()) {
            Meter m = it.next().getValue();
            if (now - m.lastHit < 60) {
                continue;
            }
            boolean empty = true;
            for (int i = 0; i < m.value.length; i++) {
                m.value[i] = Math.max(0, m.value[i] - 6);
                empty &= m.value[i] <= 0;
            }
            if (empty || now - m.lastHit > 1200) {
                it.remove();
            }
        }
        FROSTBITTEN.values().removeIf(until -> until < now);
    }

    public static void forget(int entityId) {
        METERS.remove(entityId);
        FROSTBITTEN.remove(entityId);
    }

    public static void clear() {
        METERS.clear();
        FROSTBITTEN.clear();
    }

    private Status() {
    }
}
