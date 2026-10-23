package io.nightbeam.runeborn.mc;

import io.nightbeam.runeborn.compat.Compat;
import io.nightbeam.runeborn.compat.Platform;
import io.nightbeam.runeborn.core.Rb;
import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.player.Session;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Stats;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Player-facing helpers: syncing, attribute application, inventory, messages. */
public final class Players {
    static final String HEALTH_ID = "player_vigor";
    static final String POISE_ID = "player_poise";
    static final String LUCK_ID = "player_discovery";

    public static void send(ServerPlayer player, byte[] payload) {
        Platform.send(player, payload);
    }

    public static void sync(ServerPlayer player) {
        send(player, Packets.sync(RbServer.data(player), ServerConfig.INSTANCE, RasBridge.levelingEnabled(),
                RasBridge.rpgLevelsPerRuneLevel()));
        Session s = RbServer.session(player);
        s.sentRunes = -1;
        s.dirty = false;
        syncVitals(player, true);
    }

    public static void syncVitals(ServerPlayer player, boolean force) {
        PlayerData d = RbServer.data(player);
        Session s = RbServer.session(player);
        double fp = d.focus();
        double st = s.stamina(d.maxStamina());
        if (force || Math.abs(fp - s.sentFp) >= 0.5 || Math.abs(st - s.sentStamina) >= 0.5 || d.runes != s.sentRunes) {
            send(player, Packets.vitals(fp, st, d.runes, d.emberLeft, d.azureLeft));
            s.sentFp = fp;
            s.sentStamina = st;
            s.sentRunes = d.runes;
        }
    }

    /** Applies Vigor, poise and discovery to vanilla attributes. Keeps the health ratio. */
    public static void applyAttributes(ServerPlayer player) {
        PlayerData d = RbServer.data(player);
        float ratio = player.getMaxHealth() > 0 ? player.getHealth() / player.getMaxHealth() : 1;
        double bonus = d.maxHealth() - 20.0;
        Compat.setModifier(player, Fx.A.MAX_HEALTH, HEALTH_ID, bonus, false);
        Compat.setModifier(player, Fx.A.KNOCKBACK_RESISTANCE, POISE_ID, Stats.poise(d.effective(Attr.ENDURANCE)), false);
        Compat.setModifier(player, Fx.A.LUCK, LUCK_ID, Stats.discovery(d.effective(Attr.ARCANE)), false);
        float max = player.getMaxHealth();
        player.setHealth(Math.max(1, Math.min(max, ratio * max)));
    }

    public static void message(ServerPlayer player, String key, Object... args) {
        player.sendSystemMessage(Component.translatable(key, args));
    }

    public static void actionBar(ServerPlayer player, String key, Object... args) {
        Compat.actionBar(player, Component.translatable(key, args));
    }

    public static void banner(ServerPlayer player, Packets.Banner kind, String title, String subtitle) {
        send(player, Packets.banner(kind, title, subtitle));
    }

    /** Gives runes, with the HUD counter animation. */
    public static void giveRunes(ServerPlayer player, long amount) {
        if (amount <= 0) {
            return;
        }
        RbServer.data(player).addRunes(amount);
        send(player, Packets.runeGain(amount));
        syncVitals(player, true);
    }

    public static void give(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        Compat.give(player, stack);
    }

    public static void give(ServerPlayer player, String path, int count) {
        ItemStack stack = RbItems.stack(path, count);
        if (!stack.isEmpty()) {
            give(player, stack);
        }
    }

    /** Counts a Runeborn item across the main inventory and offhand. */
    public static int count(ServerPlayer player, String path) {
        Inventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (RbItems.is(s, path)) {
                n += s.getCount();
            }
        }
        return n;
    }

    /** Removes {@code count} of an item; returns false (removing nothing) if there are not enough. */
    public static boolean take(ServerPlayer player, String path, int count) {
        if (player.isCreative()) {
            return true;
        }
        if (count(player, path) < count) {
            return false;
        }
        Inventory inv = player.getInventory();
        int left = count;
        for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (RbItems.is(s, path)) {
                int n = Math.min(left, s.getCount());
                s.shrink(n);
                left -= n;
            }
        }
        return true;
    }

    public static boolean isRuneborn(ServerPlayer player) {
        return RbServer.running() && RbServer.data(player).hasOrigin();
    }

    public static String displayDamage(double amount) {
        return String.valueOf(Math.round(amount * Rb.DISPLAY_SCALE));
    }

    private Players() {
    }
}
