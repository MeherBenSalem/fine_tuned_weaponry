package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.feat.Feat;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.mob.EliteAffix;
import io.nightbeam.runeborn.core.mob.Loot;
import io.nightbeam.runeborn.core.mob.MobLevels;
import io.nightbeam.runeborn.core.mob.Runes;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Zone levels, champions, rune rewards and drops for ordinary mobs. */
public final class Mobs {
    public static final String SEEN = "runeborn.seen";
    public static final String LEVEL = "runeborn.lvl.";
    public static final String ELITE = "runeborn.elite";
    private static final Random RANDOM = new Random();
    private static final Map<Integer, Mob> ELITES = new HashMap<>();

    public static int level(Entity e) {
        for (String tag : Compat.tags(e)) {
            if (tag.startsWith(LEVEL)) {
                try {
                    return Integer.parseInt(tag.substring(LEVEL.length()));
                } catch (NumberFormatException ignored) {
                    return 1;
                }
            }
        }
        return 1;
    }

    public static boolean isElite(Entity e) {
        return Compat.tags(e).contains(ELITE);
    }

    public static List<EliteAffix> affixes(Entity e) {
        List<EliteAffix> list = new ArrayList<>(2);
        for (String tag : Compat.tags(e)) {
            EliteAffix a = EliteAffix.fromTag(tag);
            if (a != null) {
                list.add(a);
            }
        }
        return list;
    }

    /** Assigns a zone level (and maybe champion status) the first time a hostile mob appears. */
    public static void onLoad(Entity entity, ServerLevel level) {
        if (!(entity instanceof Mob mob)) {
            return;
        }
        if (Compat.tags(mob).contains(SEEN)) {
            if (isElite(mob)) {
                ELITES.put(mob.getId(), mob);
            }
            return;
        }
        if (!(mob instanceof Enemy)) {
            return;
        }
        mob.addTag(SEEN);
        ServerConfig cfg = ServerConfig.INSTANCE;
        if (!cfg.mobLevelsEnabled || BossManager.isBoss(mob)) {
            return;
        }
        BlockPos spawn = Compat.spawnPos(level);
        double dx = mob.getX() - spawn.getX();
        double dz = mob.getZ() - spawn.getZ();
        MobLevels.Realm realm = MobLevels.Realm.of(Compat.dimensionId(level));
        int lvl = MobLevels.level(cfg, realm, Math.sqrt(dx * dx + dz * dz), mob.getY(), RANDOM);
        mob.addTag(LEVEL + lvl);
        double hp = MobLevels.healthMultiplier(cfg, lvl) - 1;
        double dmg = MobLevels.damageMultiplier(cfg, lvl) - 1;
        if (hp > 0) {
            Compat.setModifier(mob, Fx.A.MAX_HEALTH, "zone_health", hp, true);
        }
        if (dmg > 0) {
            Compat.setModifier(mob, Fx.A.ATTACK_DAMAGE, "zone_damage", dmg, true);
        }
        if (Bosses.vanilla(Compat.entityTypeId(mob)) == null && RANDOM.nextDouble() < cfg.eliteChance && !mob.isBaby()) {
            makeElite(mob, lvl);
        }
        mob.setHealth(mob.getMaxHealth());
    }

    public static void makeElite(Mob mob, int level) {
        mob.addTag(ELITE);
        EliteAffix first = EliteAffix.random(RANDOM);
        mob.addTag(first.tag());
        EliteAffix second = null;
        if (level >= 30) {
            second = EliteAffix.random(RANDOM);
            if (second != first) {
                mob.addTag(second.tag());
            } else {
                second = null;
            }
        }
        Compat.setModifier(mob, Fx.A.MAX_HEALTH, "elite_health", 1.5, true);
        Compat.setModifier(mob, Fx.A.ATTACK_DAMAGE, "elite_damage", 0.4, true);
        for (EliteAffix a : second == null ? List.of(first) : List.of(first, second)) {
            switch (a) {
                case IRONCLAD -> {
                    Compat.addEffect(mob, Fx.E.RESISTANCE, -1, 1);
                    Compat.setModifier(mob, Fx.A.ARMOR, "elite_armor", 6, false);
                }
                case FRENZIED -> {
                    Compat.addEffect(mob, Fx.E.SPEED, -1, 0);
                    Compat.addEffect(mob, Fx.E.STRENGTH, -1, 0);
                }
                case WARDED -> {
                    Compat.addEffect(mob, Fx.E.REGENERATION, -1, 0);
                    Compat.addEffect(mob, Fx.E.ABSORPTION, -1, 2);
                }
                case COLOSSAL -> {
                    Compat.setModifier(mob, Fx.A.SCALE, "elite_scale", 0.35, true);
                    Compat.setModifier(mob, Fx.A.MAX_HEALTH, "elite_colossal", 0.5, true);
                }
                case BLAZING -> Compat.addEffect(mob, Fx.E.FIRE_RESISTANCE, -1, 0);
                default -> {
                }
            }
        }
        StringBuilder title = new StringBuilder(first.title);
        if (second != null) {
            title.append(' ').append(second.title);
        }
        Component name = Component.literal(title + " ").withStyle(ChatFormatting.GOLD)
                .append(mob.getType().getDescription().copy().withStyle(ChatFormatting.YELLOW));
        mob.setCustomName(name);
        mob.setHealth(mob.getMaxHealth());
        ELITES.put(mob.getId(), mob);
    }

    /** Champion auras; called every 10 ticks. */
    public static void tickAuras() {
        ELITES.values().removeIf(m -> m.isRemoved() || !m.isAlive());
        for (Mob m : ELITES.values()) {
            if (!(m.level() instanceof ServerLevel level)) {
                continue;
            }
            List<EliteAffix> affixes = affixes(m);
            int color = affixes.isEmpty() ? 0xE8C266 : affixes.get(0).color;
            Compat.dust(level, color, 1.0f, m.getX(), m.getY() + m.getBbHeight() * 0.5, m.getZ(), 3, m.getBbWidth() * 0.6);
        }
    }

    public static void onDeath(LivingEntity entity, DamageSource source) {
        Status.forget(entity.getId());
        ELITES.remove(entity.getId());
        if (!RbServer.running() || !(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (entity instanceof ServerPlayer player) {
            onPlayerDeath(player);
            return;
        }
        if (BossManager.onDeath(entity)) {
            return;
        }
        ServerPlayer killer = killer(entity, source);
        if (killer == null || !(entity instanceof Mob mob)) {
            return;
        }
        PlayerData d = RbServer.data(killer);
        ServerConfig cfg = ServerConfig.INSTANCE;
        int lvl = level(mob);
        boolean elite = isElite(mob);
        double baseHp = Compat.attrBase(mob, Fx.A.MAX_HEALTH);
        MobLevels.Realm realm = MobLevels.Realm.of(Compat.dimensionId(level));
        long runes = Runes.forKill(cfg, baseHp, lvl, realm, elite);
        if (!(mob instanceof Enemy)) {
            runes = Math.max(1, runes / 4);
        }
        Players.giveRunes(killer, runes);
        d.kills++;
        if (elite) {
            d.champions++;
            Feats.grant(killer, Feat.CHAMPION);
            Players.banner(killer, Packets.Banner.CHAMPION_SLAIN, Lang.en(Lang.B_CHAMPION),
                    mob.getName().getString() + " · +" + Runes.format(runes));
            Fx.sound(mob, Fx.S.CHIME, 1f, 1.2f);
        }
        if (d.shard() == CrownShard.BLOOM) {
            killer.heal(killer.getMaxHealth() * 0.03f);
        }
        if (mob instanceof Enemy) {
            for (Loot.Drop drop : Loot.forMob(lvl, elite, Compat.luck(killer), RANDOM)) {
                ItemStack stack = RbItems.stack(drop.id(), drop.count());
                if (!stack.isEmpty()) {
                    level.addFreshEntity(new ItemEntity(level, mob.getX(), mob.getY() + 0.5, mob.getZ(), stack));
                }
            }
        }
    }

    static ServerPlayer killer(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer p) {
            return p;
        }
        return entity.getKillCredit() instanceof ServerPlayer p ? p : null;
    }

    private static void onPlayerDeath(ServerPlayer player) {
        PlayerData d = RbServer.data(player);
        d.deaths++;
        if (ServerConfig.INSTANCE.dropRunesOnDeath) {
            if (d.runes > 0) {
                d.lost = new PlayerData.LostRunes(d.runes, Compat.dimensionId(player.level()), player.getX(),
                        player.getY() + 0.5, player.getZ());
                d.runes = 0;
                Players.message(player, Lang.MSG_RUNES_LOST);
            } else {
                d.lost = null;
            }
        }
        Players.banner(player, Packets.Banner.YOU_DIED, Lang.en(Lang.B_DIED), "");
        Feats.grant(player, Feat.DIED);
        Players.sync(player);
        RbServer.save(player);
    }

    private Mobs() {
    }
}
