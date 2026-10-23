package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.feat.Feat;
import io.nightbeam.runeborn.core.lang.Lang;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Leveling;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Origin selection and spending runes on levels. */
public final class Progression {
    public static void chooseOrigin(ServerPlayer player, String id) {
        PlayerData d = RbServer.data(player);
        Origin origin = Origin.byId(id);
        if (origin == null || d.hasOrigin()) {
            return;
        }
        d.origin = origin.id();
        d.attrs = origin.attrs();
        d.setFocus(d.maxFocus());
        d.refillFlasks();
        if (!d.kitGiven) {
            d.kitGiven = true;
            ItemStack weapon = RbItems.stack(origin.weapon, 1);
            if (!weapon.isEmpty() && origin.weaponAffinity != io.nightbeam.runeborn.core.weapon.Affinity.STANDARD) {
                Stacks.setAffinity(weapon, origin.weaponAffinity);
            }
            Players.give(player, weapon);
            Players.give(player, "ember_flask", 1);
            Players.give(player, "azure_flask", 1);
            for (Origin.KitItem item : origin.kit()) {
                Players.give(player, item.id(), item.count());
            }
            if (RbItems.shrineItem != null) {
                Players.give(player, new ItemStack(RbItems.shrineItem, 1));
            }
        }
        Players.applyAttributes(player);
        player.setHealth(player.getMaxHealth());
        Players.banner(player, Packets.Banner.RISE, Lang.en(Lang.B_RISE), origin.displayName);
        Feats.grant(player, Feat.ROOT);
        Fx.sound(player, Fx.S.SHRINE_KINDLE, 1f, 0.8f);
        Fx.helix((ServerLevel) player.level(), Fx.P.SOUL_FIRE, player.position(), 1.1, 2.4, 30);
        Players.sync(player);
        RbServer.save(player);
    }

    public static void levelUp(ServerPlayer player, int[] target) {
        PlayerData d = RbServer.data(player);
        if (target == null || target.length != Attr.COUNT || !d.hasOrigin()) {
            return;
        }
        if (!RasBridge.levelingEnabled()) {
            Players.actionBar(player, Lang.MSG_LEVEL_LOCKED);
            return;
        }
        for (int i = 0; i < Attr.COUNT; i++) {
            if (target[i] < d.attrs[i] || target[i] > Attr.MAX) {
                return;
            }
        }
        int from = d.level();
        int to = Leveling.level(target);
        if (to <= from) {
            return;
        }
        long cost = Leveling.costRange(from, to - from, ServerConfig.INSTANCE.levelCostMultiplier);
        if (!player.isCreative() && !d.spendRunes(cost)) {
            Players.actionBar(player, Lang.NOT_ENOUGH_RUNES);
            return;
        }
        d.attrs = target.clone();
        Players.applyAttributes(player);
        RasBridge.onRuneLevels(player, to - from);
        Feats.levels(player, d);
        Fx.sound(player, Fx.S.LEVEL_UP, 1f, 1f);
        Fx.helix((ServerLevel) player.level(), Fx.P.END_ROD, player.position(), 1.0, 2.2, 20);
        Players.sync(player);
    }

    private Progression() {
    }
}
