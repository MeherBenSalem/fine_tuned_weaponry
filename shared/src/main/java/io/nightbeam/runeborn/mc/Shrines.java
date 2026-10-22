package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.item.Materials;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.net.Packets.ShrineAction;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.shop.Merchant;
import io.nightbeam.runeborn.core.shrine.ShrineNames;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.weapon.Affinity;
import io.nightbeam.runeborn.core.weapon.Upgrades;
import io.nightbeam.runeborn.core.weapon.WeaponSpec;
import io.nightbeam.runeborn.core.weapon.Weapons;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Ember Shrines: kindling, resting, fast travel and every shrine-menu action. */
public final class Shrines {
    /** Right-click on a shrine block. */
    public static void use(ServerPlayer player, BlockPos pos) {
        if (!RbServer.running()) {
            return;
        }
        PlayerData d = RbServer.data(player);
        if (!d.hasOrigin()) {
            Players.send(player, Packets.open(Packets.Screen.ORIGIN, "", "", 0, 0, 0));
            return;
        }
        ServerLevel level = (ServerLevel) player.level();
        String dim = Compat.dimensionId(level);
        int index = d.shrineIndex(dim, pos.getX(), pos.getY(), pos.getZ());
        ShrineRecord record;
        if (index < 0) {
            String name = ShrineNames.generate(Compat.biomeId(level, pos), dim, pos.getX(), pos.getY(), pos.getZ());
            record = new ShrineRecord(name, dim, pos.getX(), pos.getY(), pos.getZ());
            d.shrines.add(record);
            Players.banner(player, Packets.Banner.SHRINE_KINDLED, Lang.en(Lang.B_KINDLED), name);
            Compat.sound(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Fx.S.SHRINE_KINDLE, 1f, 1f);
            Fx.column(level, Fx.P.FLAME, Vec3.atBottomCenterOf(pos).add(0, 1, 0), 3, 30, 0.2);
            Fx.burst(level, Fx.P.END_ROD, Vec3.atCenterOf(pos).add(0, 1, 0), 40, 0.6, 0.08);
        } else {
            record = d.shrines.get(index);
        }
        Session s = RbServer.session(player);
        s.shrineDimension = dim;
        s.shrineX = pos.getX();
        s.shrineY = pos.getY();
        s.shrineZ = pos.getZ();
        if (!enemiesNear(player)) {
            rest(player, record, false);
        }
        Players.sync(player);
        Players.send(player, Packets.open(Packets.Screen.SHRINE, record.name, dim, pos.getX(), pos.getY(), pos.getZ()));
    }

    private static boolean enemiesNear(ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(16);
        return !player.level().getEntitiesOfClass(Mob.class, box, m -> m.getTarget() == player && m.isAlive()).isEmpty();
    }

    private static void rest(ServerPlayer player, ShrineRecord record, boolean effects) {
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        player.setHealth(player.getMaxHealth());
        d.setFocus(d.maxFocus());
        s.stamina = d.maxStamina();
        d.refillFlasks();
        Compat.clearHarmful(player);
        Status.forget(player.getId());
        d.rested = record.copy();
        if (ServerConfig.INSTANCE.shrineSetsRespawn) {
            ServerLevel level = (ServerLevel) player.level();
            Compat.setRespawn(player, level, standingSpot(level, new BlockPos(record.x, record.y, record.z)), player.getYRot());
        }
        if (effects) {
            Fx.sound(player, Fx.S.SHRINE_REST, 1f, 1f);
            Fx.burst((ServerLevel) player.level(), Fx.P.HAPPY, player, 16, 0.05);
        }
    }

    /** An air block next to the shrine where a player can stand, for respawns and travel. */
    static BlockPos standingSpot(ServerLevel level, BlockPos shrine) {
        BlockPos[] around = {shrine.south(), shrine.north(), shrine.east(), shrine.west(), shrine.south().east(),
                shrine.north().west(), shrine.above()};
        for (BlockPos p : around) {
            for (int dy = 0; dy >= -2; dy--) {
                BlockPos feet = p.above(dy);
                if (level.getBlockState(feet).isAir() && level.getBlockState(feet.above()).isAir()
                        && !level.getBlockState(feet.below()).isAir()) {
                    return feet;
                }
            }
        }
        return shrine.above();
    }

    /** True when the player is next to the shrine they last opened, and it still stands. */
    public static boolean near(ServerPlayer player) {
        Session s = RbServer.session(player);
        if (!s.hasShrine() || !s.shrineDimension.equals(Compat.dimensionId(player.level()))) {
            return false;
        }
        BlockPos pos = new BlockPos(s.shrineX, s.shrineY, s.shrineZ);
        if (player.distanceToSqr(Vec3.atCenterOf(pos)) > 8 * 8) {
            return false;
        }
        return player.level().getBlockState(pos).getBlock() == RbItems.shrineBlock;
    }

    /** Shrine menu actions from the client. */
    public static void action(ServerPlayer player, ShrineAction action, int a, int b, String str, int[] attrs) {
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        if (!near(player)) {
            Players.actionBar(player, Lang.MSG_TOO_FAR);
            return;
        }
        double smithMult = ServerConfig.INSTANCE.smithingCostMultiplier;
        switch (action) {
            case REST -> {
                if (enemiesNear(player)) {
                    return;
                }
                int idx = d.shrineIndex(s.shrineDimension, s.shrineX, s.shrineY, s.shrineZ);
                if (idx >= 0) {
                    rest(player, d.shrines.get(idx), true);
                }
            }
            case TRAVEL -> travel(player, d, s, a);
            case UPGRADE -> upgrade(player, d, a, smithMult);
            case INFUSE -> infuse(player, d, a, b, smithMult);
            case BUY -> buy(player, d, a, Math.max(1, Math.min(64, b)));
            case EXCHANGE -> exchange(player, str, a);
            case EQUIP_SHARD -> {
                if (d.shards.contains(str)) {
                    d.equippedShard = str;
                    Players.applyAttributes(player);
                    Fx.sound(player, Fx.S.SHARD, 1f, 1f);
                }
            }
            case FLASK_SPLIT -> {
                d.flaskEmberShare = Math.max(0, Math.min(d.flaskCharges, a));
                d.refillFlasks();
            }
            case REBIRTH -> rebirth(player, d, attrs);
        }
        Players.sync(player);
    }

    private static void travel(ServerPlayer player, PlayerData d, Session s, int index) {
        ServerConfig cfg = ServerConfig.INSTANCE;
        if (!cfg.shrineFastTravel || index < 0 || index >= d.shrines.size()) {
            return;
        }
        long now = RbServer.now();
        if (cfg.shrineTravelCooldownSeconds > 0 && now - s.lastTravelAt < cfg.shrineTravelCooldownSeconds * 20L) {
            Players.actionBar(player, Lang.MSG_COOLDOWN);
            return;
        }
        ShrineRecord target = d.shrines.get(index);
        ServerLevel level = Compat.level(RbServer.server(), target.dimension);
        BlockPos pos = new BlockPos(target.x, target.y, target.z);
        if (level == null || level.getBlockState(pos).getBlock() != RbItems.shrineBlock) {
            d.shrines.remove(index);
            Players.actionBar(player, Lang.MSG_TRAVEL_FAIL);
            return;
        }
        Fx.burst((ServerLevel) player.level(), Fx.P.SOUL_FIRE, player, 40, 0.1);
        Fx.sound(player, Fx.S.TELEPORT, 1f, 0.8f);
        BlockPos spot = standingSpot(level, pos);
        Compat.teleport(player, level, spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5, player.getYRot(), player.getXRot());
        s.lastTravelAt = now;
        s.shrineDimension = target.dimension;
        s.shrineX = target.x;
        s.shrineY = target.y;
        s.shrineZ = target.z;
        RbServer.later(2, () -> {
            Fx.burst(level, Fx.P.SOUL_FIRE, player, 40, 0.1);
            Fx.sound(player, Fx.S.TELEPORT, 1f, 1.2f);
        });
    }

    private static void upgrade(ServerPlayer player, PlayerData d, int slot, double mult) {
        ItemStack stack = slotStack(player, slot);
        WeaponSpec spec = RbItems.weapon(stack);
        if (spec == null) {
            return;
        }
        int cur = Stacks.upgrade(stack);
        Upgrades.Cost cost = Upgrades.next(spec, cur, mult);
        if (cost == null) {
            return;
        }
        if (!player.isCreative() && d.runes < cost.runes()) {
            Players.actionBar(player, Lang.NOT_ENOUGH_RUNES);
            return;
        }
        if (!Players.take(player, cost.materialId(), cost.count())) {
            return;
        }
        if (!player.isCreative()) {
            d.spendRunes(cost.runes());
        }
        Stacks.setUpgrade(stack, cur + 1);
        Fx.sound(player, Fx.S.SMITH, 1f, 0.9f + (cur + 1) * 0.01f);
        Fx.burst((ServerLevel) player.level(), Fx.P.LAVA, player, 6, 0.1);
    }

    private static void infuse(ServerPlayer player, PlayerData d, int slot, int affinityIndex, double mult) {
        ItemStack stack = slotStack(player, slot);
        WeaponSpec spec = RbItems.weapon(stack);
        if (spec == null || !spec.infusable() || affinityIndex < 0 || affinityIndex >= Affinity.ALL.length) {
            return;
        }
        Affinity affinity = Affinity.ALL[affinityIndex];
        if (affinity != Affinity.STANDARD && !player.isCreative() && Players.count(player, affinity.id() + "_whetstone") <= 0) {
            return;
        }
        long cost = Upgrades.infusionCost(mult);
        if (!player.isCreative() && !d.spendRunes(cost)) {
            Players.actionBar(player, Lang.NOT_ENOUGH_RUNES);
            return;
        }
        Stacks.setAffinity(stack, affinity);
        Fx.sound(player, Fx.S.SMITH, 1f, 1.3f);
        Compat.dust((ServerLevel) player.level(), affinity.color, 1.2f, player.getX(), player.getY() + 1, player.getZ(), 30, 0.6);
    }

    private static void buy(ServerPlayer player, PlayerData d, int offerIndex, int count) {
        Merchant.Offer offer = Merchant.get(offerIndex);
        if (offer == null) {
            return;
        }
        if (offer.sovereignsRequired() > d.sovereignsFelled() && !player.isCreative()) {
            return;
        }
        WeaponSpec weapon = Weapons.get(offer.itemId());
        if (weapon != null || Materials.get(offer.itemId()) != null && Materials.get(offer.itemId()).stack() == 1) {
            count = 1;
        }
        long price = offer.price(ServerConfig.INSTANCE.merchantPriceMultiplier) * count;
        if (!player.isCreative() && !d.spendRunes(price)) {
            Players.actionBar(player, Lang.NOT_ENOUGH_RUNES);
            Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
            return;
        }
        Players.give(player, offer.itemId(), offer.count() * count);
        Fx.sound(player, Fx.S.BUY, 1f, 1f);
    }

    private static void exchange(ServerPlayer player, String bossId, int choice) {
        BossSpec boss = Bosses.get(bossId);
        if (boss == null || choice < 0 || choice >= boss.relics().size()) {
            return;
        }
        if (!Players.take(player, boss.relicId(), 1)) {
            return;
        }
        Players.give(player, boss.relics().get(choice), 1);
        Fx.sound(player, Fx.S.SHARD, 1f, 0.8f);
        Fx.burst((ServerLevel) player.level(), Fx.P.END_ROD, player, 30, 0.1);
    }

    private static void rebirth(ServerPlayer player, PlayerData d, int[] attrs) {
        if (attrs == null || attrs.length != Attr.COUNT) {
            return;
        }
        Origin origin = d.originEnum();
        int[] floor = origin == null ? PlayerData.defaultAttrs() : origin.attrs();
        int sum = 0;
        int current = 0;
        for (int i = 0; i < Attr.COUNT; i++) {
            if (attrs[i] < floor[i] || attrs[i] > Attr.MAX) {
                return;
            }
            sum += attrs[i];
            current += d.attrs[i];
        }
        if (sum != current || !Players.take(player, "rebirth_tear", 1)) {
            return;
        }
        d.attrs = attrs.clone();
        Players.applyAttributes(player);
        Fx.sound(player, Fx.S.LEVEL_UP, 1f, 0.7f);
        Fx.helix((ServerLevel) player.level(), Fx.P.SOUL_FIRE, player.position(), 1.1, 2.4, 24);
    }

    static ItemStack slotStack(ServerPlayer player, int slot) {
        if (slot < 0 || slot > 8) {
            return ItemStack.EMPTY;
        }
        return player.getInventory().getItem(slot);
    }

    private Shrines() {
    }
}
