package io.nightbeam.runeborn.core.player;

import io.nightbeam.runeborn.core.boss.BossSpec;
import io.nightbeam.runeborn.core.boss.Bosses;
import io.nightbeam.runeborn.core.boss.CrownShard;
import io.nightbeam.runeborn.core.origin.Origin;
import io.nightbeam.runeborn.core.shrine.ShrineRecord;
import io.nightbeam.runeborn.core.stat.Attr;
import io.nightbeam.runeborn.core.stat.Leveling;
import io.nightbeam.runeborn.core.stat.Stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Everything persisted for one player. Plain public fields so Gson can read and write it;
 * stored per world under {@code runeborn/players/<uuid>.json}.
 */
public final class PlayerData {
    public static final int MAX_FLASK_CHARGES = 14;
    public static final int MAX_FLASK_POTENCY = 12;
    public static final int START_FLASK_CHARGES = 4;

    public int version = 1;
    /** Empty until the player picks an origin. */
    public String origin = "";
    public int[] attrs = defaultAttrs();
    public long runes;
    public long runesEarned;
    public LostRunes lost;
    public int flaskCharges = START_FLASK_CHARGES;
    /** How many of {@link #flaskCharges} are Ember (health); the rest are Azure (focus). */
    public int flaskEmberShare = 3;
    public int flaskPotency;
    public int emberLeft = 3;
    public int azureLeft = 1;
    /** Current focus, or negative for "full". */
    public double fp = -1;
    public List<ShrineRecord> shrines = new ArrayList<>();
    public ShrineRecord rested;
    public List<String> shards = new ArrayList<>();
    public String equippedShard = "";
    public List<String> felled = new ArrayList<>();
    public int kills;
    public int deaths;
    public int champions;
    public boolean kitGiven;

    public static final class LostRunes {
        public long amount;
        public String dimension;
        public double x;
        public double y;
        public double z;

        public LostRunes() {
        }

        public LostRunes(long amount, String dimension, double x, double y, double z) {
            this.amount = amount;
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static int[] defaultAttrs() {
        int[] a = new int[Attr.COUNT];
        Arrays.fill(a, 10);
        return a;
    }

    /** Repairs anything a hand-edited or older file might have broken. */
    public PlayerData sanitize() {
        if (origin == null) {
            origin = "";
        }
        if (attrs == null || attrs.length != Attr.COUNT) {
            attrs = defaultAttrs();
        }
        for (int i = 0; i < attrs.length; i++) {
            attrs[i] = Math.max(Attr.MIN, Math.min(Attr.MAX, attrs[i]));
        }
        runes = Math.max(0, runes);
        flaskCharges = Math.max(1, Math.min(MAX_FLASK_CHARGES, flaskCharges));
        flaskEmberShare = Math.max(0, Math.min(flaskCharges, flaskEmberShare));
        flaskPotency = Math.max(0, Math.min(MAX_FLASK_POTENCY, flaskPotency));
        emberLeft = Math.max(0, Math.min(flaskEmberShare, emberLeft));
        azureLeft = Math.max(0, Math.min(flaskCharges - flaskEmberShare, azureLeft));
        if (shrines == null) {
            shrines = new ArrayList<>();
        }
        shrines.removeIf(s -> s == null || s.dimension == null);
        if (shards == null) {
            shards = new ArrayList<>();
        }
        if (felled == null) {
            felled = new ArrayList<>();
        }
        if (equippedShard == null || !shards.contains(equippedShard)) {
            equippedShard = "";
        }
        return this;
    }

    public boolean hasOrigin() {
        return !origin.isEmpty();
    }

    public Origin originEnum() {
        return Origin.byId(origin);
    }

    public int level() {
        return Leveling.level(attrs);
    }

    public int attr(Attr attr) {
        return attrs[attr.ordinal()];
    }

    /** Attributes including crown shard bonuses; use these for every gameplay calculation. */
    public int[] effectiveAttrs() {
        int[] a = attrs.clone();
        if (shard() == CrownShard.GRAFTED) {
            for (int i = 0; i < a.length; i++) {
                a[i] = Math.min(Attr.MAX, a[i] + 5);
            }
        }
        return a;
    }

    public int effective(Attr attr) {
        return effectiveAttrs()[attr.ordinal()];
    }

    public CrownShard shard() {
        if (equippedShard.isEmpty()) {
            return null;
        }
        for (BossSpec boss : Bosses.all()) {
            if (equippedShard.equals(boss.shardId())) {
                return boss.shard();
            }
        }
        return null;
    }

    public double maxHealth() {
        double hp = Stats.maxHealth(effective(Attr.VIGOR));
        return shard() == CrownShard.FROST ? hp * 1.15 : hp;
    }

    public double maxFocus() {
        return Stats.maxFocus(effective(Attr.MIND));
    }

    public double maxStamina() {
        return Stats.maxStamina(effective(Attr.ENDURANCE));
    }

    public double focus() {
        double max = maxFocus();
        return fp < 0 ? max : Math.min(fp, max);
    }

    public void setFocus(double value) {
        double max = maxFocus();
        fp = value >= max ? -1 : Math.max(0, value);
    }

    public int azureShare() {
        return flaskCharges - flaskEmberShare;
    }

    public void refillFlasks() {
        emberLeft = flaskEmberShare;
        azureLeft = azureShare();
    }

    /** Health restored by one Ember Flask, as a fraction of max health. */
    public double emberHealFraction() {
        return 0.35 + 0.03 * flaskPotency;
    }

    /** Focus restored by one Azure Flask. */
    public double azureRestore() {
        return 45 + 9 * flaskPotency;
    }

    public void addRunes(long amount) {
        if (amount <= 0) {
            return;
        }
        runes = saturatingAdd(runes, amount);
        runesEarned = saturatingAdd(runesEarned, amount);
    }

    public boolean spendRunes(long amount) {
        if (amount < 0 || runes < amount) {
            return false;
        }
        runes -= amount;
        return true;
    }

    public int shrineIndex(String dimension, int x, int y, int z) {
        for (int i = 0; i < shrines.size(); i++) {
            if (shrines.get(i).at(dimension, x, y, z)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasFelled(String bossId) {
        return felled.contains(bossId);
    }

    /** Number of distinct sovereigns (summonable bosses) felled; gates merchant stock. */
    public int sovereignsFelled() {
        int n = 0;
        for (String id : felled) {
            BossSpec boss = Bosses.get(id);
            if (boss != null) {
                n++;
            }
        }
        return n;
    }

    private static long saturatingAdd(long a, long b) {
        long r = a + b;
        return r < a ? Long.MAX_VALUE : r;
    }
}
