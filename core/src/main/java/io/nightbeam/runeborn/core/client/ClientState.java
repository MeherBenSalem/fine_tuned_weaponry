package io.nightbeam.runeborn.core.client;

import io.nightbeam.runeborn.core.net.Packets;
import io.nightbeam.runeborn.core.player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Everything the client knows about its own Runeborn state. Main-thread only. */
public final class ClientState {
    public static Packets.SyncView view;

    public static float fp;
    public static float stamina;
    public static long runes;
    public static int emberLeft;
    public static int azureLeft;

    // Animated display values
    public static float shownRunes;
    public static float hpChip = 1;
    public static float fpChip = 1;
    public static float staminaChip = 1;
    public static long pendingGain;
    public static float gainTime = -1000;

    public static Packets.BannerMsg banner;
    public static float bannerTime = -1000;

    public static Packets.Target target;
    public static float targetTime = -1000;
    public static float targetChip = 1;

    public static List<Packets.BossBar> bosses = new ArrayList<>();
    public static final Map<Integer, Float> bossChips = new HashMap<>();
    public static float bossesTime = -1000;

    public static float shakeUntil = -1000;
    public static float shakeStrength;
    public static float flashTime = -1000;
    public static int flashColor;

    /** Client-tracked dodge cooldown, in client ticks. */
    public static float dodgeReadyAt;

    public static boolean ready() {
        return view != null;
    }

    public static PlayerData data() {
        return view == null ? null : view.data;
    }

    public static void reset() {
        view = null;
        fp = 0;
        stamina = 0;
        runes = 0;
        shownRunes = 0;
        banner = null;
        target = null;
        bosses = new ArrayList<>();
        bossChips.clear();
        pendingGain = 0;
    }

    private ClientState() {
    }
}
