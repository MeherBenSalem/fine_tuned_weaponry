package io.nightbeam.runeborn.core.net;

import io.nightbeam.runeborn.core.config.ServerConfig;
import io.nightbeam.runeborn.core.player.PlayerData;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.weapon.StatusType;

import java.util.ArrayList;
import java.util.List;

/**
 * Packet layouts. Each payload starts with a varint id. Server-to-client ids are below 64,
 * client-to-server ids are 64 and up.
 */
public final class Packets {
    // Server -> client
    public static final int SYNC = 1;
    public static final int VITALS = 2;
    public static final int BANNER = 3;
    public static final int TARGET = 4;
    public static final int BOSSES = 5;
    public static final int OPEN = 6;
    public static final int RUNE_GAIN = 7;
    public static final int FX = 8;

    // Client -> server
    public static final int CHOOSE_ORIGIN = 64;
    public static final int LEVEL_UP = 65;
    public static final int SHRINE = 66;
    public static final int DODGE = 67;
    public static final int OPEN_CHARACTER = 68;
    public static final int USE_ART = 69;

    public static final int MAX_LIST = 512;

    public enum Banner {
        SHRINE_KINDLED, YOU_DIED, SOVEREIGN_FELLED, RUNES_RECOVERED, BOSS_APPEARS, RISE, CHAMPION_SLAIN, LEVEL_UP,
        CROWN_SHARD
    }

    public enum Screen {
        SHRINE, ORIGIN, CHARACTER
    }

    public enum ShrineAction {
        REST, TRAVEL, UPGRADE, INFUSE, BUY, EXCHANGE, EQUIP_SHARD, FLASK_SPLIT, REBIRTH
    }

    /** Client-side effects the server asks for (screen shake, flashes). */
    public enum Fx {
        SHAKE, FLASH_RED, FLASH_GOLD, DODGE
    }

    // ------------------------------------------------------------------ server -> client

    public static byte[] sync(PlayerData d, ServerConfig cfg) {
        Buf b = Buf.writer().varInt(SYNC);
        b.str(d.origin).ints(d.attrs).varLong(d.runes).varLong(d.runesEarned);
        b.varInt(d.flaskCharges).varInt(d.flaskEmberShare).varInt(d.flaskPotency).varInt(d.emberLeft).varInt(d.azureLeft);
        b.bool(d.lost != null);
        if (d.lost != null) {
            b.varLong(d.lost.amount).str(d.lost.dimension).f64(d.lost.x).f64(d.lost.y).f64(d.lost.z);
        }
        b.varInt(d.shrines.size());
        for (ShrineRecord s : d.shrines) {
            writeShrine(b, s);
        }
        b.bool(d.rested != null);
        if (d.rested != null) {
            writeShrine(b, d.rested);
        }
        writeStrings(b, d.shards);
        b.str(d.equippedShard);
        writeStrings(b, d.felled);
        b.varInt(d.kills).varInt(d.deaths).varInt(d.champions);
        // Settings the client needs for previews.
        b.f64(cfg.levelCostMultiplier).f64(cfg.smithingCostMultiplier).f64(cfg.merchantPriceMultiplier);
        b.bool(cfg.staminaEnabled).bool(cfg.shrineFastTravel).bool(cfg.requireClassSelection);
        return b.bytes();
    }

    /** Client view of the synced state. */
    public static final class SyncView {
        public final PlayerData data = new PlayerData();
        public double levelCostMultiplier = 1;
        public double smithingCostMultiplier = 1;
        public double merchantPriceMultiplier = 1;
        public boolean staminaEnabled = true;
        public boolean fastTravel = true;
        public boolean requireClass = true;
    }

    public static SyncView readSync(Buf b) {
        SyncView v = new SyncView();
        PlayerData d = v.data;
        d.origin = b.readStr();
        d.attrs = b.readInts(Attr.COUNT);
        d.runes = b.readVarLong();
        d.runesEarned = b.readVarLong();
        d.flaskCharges = b.readVarInt();
        d.flaskEmberShare = b.readVarInt();
        d.flaskPotency = b.readVarInt();
        d.emberLeft = b.readVarInt();
        d.azureLeft = b.readVarInt();
        if (b.readBool()) {
            d.lost = new PlayerData.LostRunes(b.readVarLong(), b.readStr(), b.readF64(), b.readF64(), b.readF64());
        }
        int n = b.readVarInt();
        if (n > MAX_LIST) {
            throw new IllegalStateException("Too many shrines");
        }
        for (int i = 0; i < n; i++) {
            d.shrines.add(readShrine(b));
        }
        if (b.readBool()) {
            d.rested = readShrine(b);
        }
        d.shards = readStrings(b);
        d.equippedShard = b.readStr();
        d.felled = readStrings(b);
        d.kills = b.readVarInt();
        d.deaths = b.readVarInt();
        d.champions = b.readVarInt();
        v.levelCostMultiplier = b.readF64();
        v.smithingCostMultiplier = b.readF64();
        v.merchantPriceMultiplier = b.readF64();
        v.staminaEnabled = b.readBool();
        v.fastTravel = b.readBool();
        v.requireClass = b.readBool();
        d.sanitize();
        return v;
    }

    public static byte[] vitals(double fp, double stamina, long runes, int emberLeft, int azureLeft) {
        return Buf.writer().varInt(VITALS).f32((float) fp).f32((float) stamina).varLong(runes)
                .varInt(emberLeft).varInt(azureLeft).bytes();
    }

    public record Vitals(float fp, float stamina, long runes, int emberLeft, int azureLeft) {
    }

    public static Vitals readVitals(Buf b) {
        return new Vitals(b.readF32(), b.readF32(), b.readVarLong(), b.readVarInt(), b.readVarInt());
    }

    public static byte[] banner(Banner kind, String title, String subtitle) {
        return Buf.writer().varInt(BANNER).varInt(kind.ordinal()).str(title).str(subtitle).bytes();
    }

    public record BannerMsg(Banner kind, String title, String subtitle) {
    }

    public static BannerMsg readBanner(Buf b) {
        int k = b.readVarInt();
        Banner kind = k >= 0 && k < Banner.values().length ? Banner.values()[k] : Banner.LEVEL_UP;
        return new BannerMsg(kind, b.readStr(), b.readStr());
    }

    public record Target(int entityId, String name, int level, float health, float maxHealth, float lastDamage,
                         boolean elite, int color, float[] status) {
    }

    public static byte[] target(Target t) {
        Buf b = Buf.writer().varInt(TARGET).varInt(t.entityId).str(t.name).varInt(t.level).f32(t.health)
                .f32(t.maxHealth).f32(t.lastDamage).bool(t.elite).varInt(t.color);
        for (int i = 0; i < StatusType.ALL.length; i++) {
            b.f32(t.status[i]);
        }
        return b.bytes();
    }

    public static Target readTarget(Buf b) {
        int id = b.readVarInt();
        String name = b.readStr();
        int level = b.readVarInt();
        float hp = b.readF32();
        float max = b.readF32();
        float dmg = b.readF32();
        boolean elite = b.readBool();
        int color = b.readVarInt();
        float[] status = new float[StatusType.ALL.length];
        for (int i = 0; i < status.length; i++) {
            status[i] = b.readF32();
        }
        return new Target(id, name, level, hp, max, dmg, elite, color, status);
    }

    public record BossBar(int entityId, String name, float health, float maxHealth, int color, int phase) {
    }

    public static byte[] bosses(List<BossBar> bars) {
        Buf b = Buf.writer().varInt(BOSSES).varInt(bars.size());
        for (BossBar bar : bars) {
            b.varInt(bar.entityId).str(bar.name).f32(bar.health).f32(bar.maxHealth).varInt(bar.color).varInt(bar.phase);
        }
        return b.bytes();
    }

    public static List<BossBar> readBosses(Buf b) {
        int n = b.readVarInt();
        if (n > 16) {
            throw new IllegalStateException("Too many boss bars");
        }
        List<BossBar> bars = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            bars.add(new BossBar(b.readVarInt(), b.readStr(), b.readF32(), b.readF32(), b.readVarInt(), b.readVarInt()));
        }
        return bars;
    }

    public record Open(Screen screen, String shrineName, String dimension, int x, int y, int z) {
    }

    public static byte[] open(Screen screen, String shrineName, String dimension, int x, int y, int z) {
        return Buf.writer().varInt(OPEN).varInt(screen.ordinal()).str(shrineName).str(dimension).varInt(x).varInt(y)
                .varInt(z).bytes();
    }

    public static Open readOpen(Buf b) {
        int s = b.readVarInt();
        Screen screen = s >= 0 && s < Screen.values().length ? Screen.values()[s] : Screen.CHARACTER;
        return new Open(screen, b.readStr(), b.readStr(), b.readVarInt(), b.readVarInt(), b.readVarInt());
    }

    public static byte[] runeGain(long amount) {
        return Buf.writer().varInt(RUNE_GAIN).varLong(amount).bytes();
    }

    public static byte[] fx(Fx fx, float strength) {
        return Buf.writer().varInt(FX).varInt(fx.ordinal()).f32(strength).bytes();
    }

    // ------------------------------------------------------------------ client -> server

    public static byte[] chooseOrigin(String originId) {
        return Buf.writer().varInt(CHOOSE_ORIGIN).str(originId).bytes();
    }

    public static byte[] levelUp(int[] targetAttrs) {
        return Buf.writer().varInt(LEVEL_UP).ints(targetAttrs).bytes();
    }

    public static byte[] shrine(ShrineAction action, int a, int b, String s) {
        return Buf.writer().varInt(SHRINE).varInt(action.ordinal()).varInt(a).varInt(b).str(s).bytes();
    }

    public static byte[] rebirth(int[] attrs) {
        return Buf.writer().varInt(SHRINE).varInt(ShrineAction.REBIRTH.ordinal()).varInt(0).varInt(0).str("")
                .ints(attrs).bytes();
    }

    public static byte[] dodge(float dx, float dz) {
        return Buf.writer().varInt(DODGE).f32(dx).f32(dz).bytes();
    }

    public static byte[] openCharacter() {
        return Buf.writer().varInt(OPEN_CHARACTER).bytes();
    }

    public static byte[] useArt() {
        return Buf.writer().varInt(USE_ART).bytes();
    }

    private static void writeShrine(Buf b, ShrineRecord s) {
        b.str(s.name).str(s.dimension).varInt(s.x).varInt(s.y).varInt(s.z);
    }

    private static ShrineRecord readShrine(Buf b) {
        return new ShrineRecord(b.readStr(), b.readStr(), b.readVarInt(), b.readVarInt(), b.readVarInt());
    }

    private static void writeStrings(Buf b, List<String> list) {
        b.varInt(list.size());
        for (String s : list) {
            b.str(s);
        }
    }

    private static List<String> readStrings(Buf b) {
        int n = b.readVarInt();
        if (n > MAX_LIST) {
            throw new IllegalStateException("List too long");
        }
        List<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(b.readStr());
        }
        return list;
    }

    private Packets() {
    }
}
