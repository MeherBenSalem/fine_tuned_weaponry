package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.item.MaterialSpec;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Right-click behaviour of materials: flasks, runes, seeds, tears, sigils and crown shards. */
public final class ItemUse {
    /** Returns true if the item did something (the caller then reports success to the game). */
    public static boolean use(ServerPlayer player, ItemStack stack) {
        MaterialSpec spec = RbItems.material(stack);
        if (spec == null || !RbServer.running()) {
            return false;
        }
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        long now = RbServer.now();
        ServerLevel level = (ServerLevel) player.level();
        switch (spec.kind()) {
            case EMBER_FLASK -> {
                if (now - s.lastFlaskAt < 24) {
                    return false;
                }
                if (d.emberLeft <= 0 && !player.isCreative()) {
                    Players.actionBar(player, Lang.MSG_FLASK_EMPTY);
                    Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
                    return false;
                }
                if (!player.isCreative()) {
                    d.emberLeft--;
                }
                s.lastFlaskAt = now;
                double bless = 1 + Stats.blessing(d.effective(Attr.FAITH));
                player.heal((float) (player.getMaxHealth() * d.emberHealFraction() * bless));
                Compat.dust(level, 0xE8743C, 1.1f, player.getX(), player.getY() + 1, player.getZ(), 24, 0.5);
                Fx.burst(level, Fx.P.FLAME, player, 6, 0.01);
                Fx.sound(player, Fx.S.DRINK, 1f, 1f);
                Compat.itemCooldown(player, stack, 24);
            }
            case AZURE_FLASK -> {
                if (now - s.lastFlaskAt < 24) {
                    return false;
                }
                if (d.azureLeft <= 0 && !player.isCreative()) {
                    Players.actionBar(player, Lang.MSG_FLASK_EMPTY);
                    Fx.sound(player, Fx.S.DENY, 0.6f, 1f);
                    return false;
                }
                if (!player.isCreative()) {
                    d.azureLeft--;
                }
                s.lastFlaskAt = now;
                d.setFocus(d.focus() + d.azureRestore());
                Compat.dust(level, 0x4F7FE0, 1.1f, player.getX(), player.getY() + 1, player.getZ(), 24, 0.5);
                Fx.sound(player, Fx.S.DRINK, 1f, 1.2f);
                Compat.itemCooldown(player, stack, 24);
            }
            case RUNE -> {
                int n = player.isShiftKeyDown() ? stack.getCount() : 1;
                stack.shrink(n);
                Players.giveRunes(player, (long) spec.value() * n);
                Fx.burst(level, Fx.P.TOTEM, player, 12, 0.2);
                Fx.sound(player, Fx.S.RUNES_PICKUP, 1f, 1.1f);
            }
            case SUNSEED -> {
                if (d.flaskCharges >= PlayerData.MAX_FLASK_CHARGES) {
                    Players.actionBar(player, Lang.MSG_SEED_MAX);
                    return false;
                }
                stack.shrink(1);
                d.flaskCharges++;
                d.flaskEmberShare++;
                d.emberLeft++;
                Players.actionBar(player, Lang.MSG_SEED);
                Fx.sound(player, Fx.S.CHIME, 1f, 1.2f);
                Fx.burst(level, Fx.P.HAPPY, player, 16, 0.1);
            }
            case HALLOWED_TEAR -> {
                if (d.flaskPotency >= PlayerData.MAX_FLASK_POTENCY) {
                    Players.actionBar(player, Lang.MSG_TEAR_MAX);
                    return false;
                }
                stack.shrink(1);
                d.flaskPotency++;
                Players.actionBar(player, Lang.MSG_TEAR);
                Fx.sound(player, Fx.S.CHIME, 1f, 0.9f);
                Fx.burst(level, Fx.P.GLOW, player, 16, 0.1);
            }
            case SIGIL -> {
                BossSpec boss = Bosses.get(spec.ref());
                if (boss == null || !BossManager.summon(player, boss)) {
                    return false;
                }
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            case CROWN_SHARD -> {
                String id = spec.id();
                if (d.shards.contains(id)) {
                    Players.actionBar(player, Lang.MSG_SHARD_KNOWN);
                    return false;
                }
                stack.shrink(1);
                d.shards.add(id);
                if (d.equippedShard.isEmpty()) {
                    d.equippedShard = id;
                    Players.applyAttributes(player);
                }
                Players.banner(player, Packets.Banner.CROWN_SHARD, Lang.en(Lang.B_SHARD), spec.name());
                Fx.sound(player, Fx.S.SHARD, 1f, 1f);
                Fx.helix(level, Fx.P.END_ROD, player.position(), 1.1, 2.6, 30);
            }
            default -> {
                return false;
            }
        }
        Players.sync(player);
        return true;
    }

    private ItemUse() {
    }
}
