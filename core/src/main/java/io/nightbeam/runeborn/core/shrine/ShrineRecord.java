package io.nightbeam.runeborn.core.shrine;

import java.util.Objects;

/** A shrine a player has kindled. Plain fields so Gson can persist it. */
public final class ShrineRecord {
    public String name;
    public String dimension;
    public int x;
    public int y;
    public int z;

    public ShrineRecord() {
    }

    public ShrineRecord(String name, String dimension, int x, int y, int z) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean at(String dimension, int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z && Objects.equals(this.dimension, dimension);
    }

    public ShrineRecord copy() {
        return new ShrineRecord(name, dimension, x, y, z);
    }
}
