package io.nightbeam.runeborn.core.mob;

import io.nightbeam.runeborn.core.config.ServerConfig;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Rune rewards and formatting. */
public final class Runes {
    private static final DecimalFormat GROUPED = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.ROOT));

    /**
     * Runes for slaying a mob.
     *
     * @param baseHealth max health before level scaling
     */
    public static long forKill(ServerConfig cfg, double baseHealth, int level, MobLevels.Realm realm, boolean elite) {
        double realmMult = switch (realm) {
            case OVERWORLD -> 1.0;
            case NETHER -> 1.5;
            case END -> 2.0;
            case OTHER -> 1.25;
        };
        double runes = Math.max(1, baseHealth) * 2.5 * realmMult * (1 + 0.1 * (level - 1));
        if (elite) {
            runes *= 6;
        }
        return Math.max(1, Math.round(runes * cfg.runeGainMultiplier));
    }

    public static long forBoss(ServerConfig cfg, long base, int partySize) {
        // Everyone who fought gets the full reward, so co-op never feels punished.
        return Math.max(1, Math.round(base * cfg.runeGainMultiplier));
    }

    public static synchronized String format(long amount) {
        return GROUPED.format(amount);
    }

    /** Compact form for tight HUD spots: 1.2k, 34.5k, 1.2M. */
    public static String compact(long amount) {
        if (amount < 10_000) {
            return format(amount);
        }
        if (amount < 1_000_000) {
            return trim(amount / 1000.0) + "k";
        }
        return trim(amount / 1_000_000.0) + "M";
    }

    private static String trim(double v) {
        String s = String.format(Locale.ROOT, "%.1f", v);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }

    private Runes() {
    }
}
