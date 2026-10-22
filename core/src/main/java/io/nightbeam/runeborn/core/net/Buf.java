package io.nightbeam.runeborn.core.net;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Minimal growable byte buffer for packet payloads. Every Minecraft version and loader carries
 * these bytes through one channel, so packet layouts are defined once, here.
 */
public final class Buf {
    private static final int MAX_STRING = 32_767;

    private final ByteArrayOutputStream out;
    private final byte[] in;
    private int pos;

    private Buf(ByteArrayOutputStream out, byte[] in) {
        this.out = out;
        this.in = in;
    }

    public static Buf writer() {
        return new Buf(new ByteArrayOutputStream(64), null);
    }

    public static Buf reader(byte[] data) {
        return new Buf(null, data);
    }

    public byte[] bytes() {
        return out.toByteArray();
    }

    public int remaining() {
        return in.length - pos;
    }

    // ---- writing

    public Buf varInt(int value) {
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
        return this;
    }

    public Buf varLong(long value) {
        while ((value & ~0x7FL) != 0) {
            out.write((int) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        out.write((int) value);
        return this;
    }

    public Buf bool(boolean value) {
        out.write(value ? 1 : 0);
        return this;
    }

    public Buf f32(float value) {
        int bits = Float.floatToIntBits(value);
        for (int i = 3; i >= 0; i--) {
            out.write((bits >>> (i * 8)) & 0xFF);
        }
        return this;
    }

    public Buf f64(double value) {
        long bits = Double.doubleToLongBits(value);
        for (int i = 7; i >= 0; i--) {
            out.write((int) ((bits >>> (i * 8)) & 0xFF));
        }
        return this;
    }

    public Buf str(String value) {
        byte[] b = (value == null ? "" : value).getBytes(StandardCharsets.UTF_8);
        if (b.length > MAX_STRING) {
            throw new IllegalArgumentException("String too long for packet: " + b.length);
        }
        varInt(b.length);
        out.write(b, 0, b.length);
        return this;
    }

    public Buf ints(int[] values) {
        varInt(values.length);
        for (int v : values) {
            varInt(v);
        }
        return this;
    }

    // ---- reading

    private int next() {
        if (pos >= in.length) {
            throw new IllegalStateException("Packet underflow");
        }
        return in[pos++] & 0xFF;
    }

    public int readVarInt() {
        int value = 0;
        int shift = 0;
        int b;
        do {
            b = next();
            value |= (b & 0x7F) << shift;
            shift += 7;
            if (shift > 35) {
                throw new IllegalStateException("VarInt too big");
            }
        } while ((b & 0x80) != 0);
        return value;
    }

    public long readVarLong() {
        long value = 0;
        int shift = 0;
        int b;
        do {
            b = next();
            value |= (long) (b & 0x7F) << shift;
            shift += 7;
            if (shift > 70) {
                throw new IllegalStateException("VarLong too big");
            }
        } while ((b & 0x80) != 0);
        return value;
    }

    public boolean readBool() {
        return next() != 0;
    }

    public float readF32() {
        int bits = 0;
        for (int i = 0; i < 4; i++) {
            bits = (bits << 8) | next();
        }
        return Float.intBitsToFloat(bits);
    }

    public double readF64() {
        long bits = 0;
        for (int i = 0; i < 8; i++) {
            bits = (bits << 8) | next();
        }
        return Double.longBitsToDouble(bits);
    }

    public String readStr() {
        int len = readVarInt();
        if (len < 0 || len > MAX_STRING || len > remaining()) {
            throw new IllegalStateException("Bad string length " + len);
        }
        String s = new String(in, pos, len, StandardCharsets.UTF_8);
        pos += len;
        return s;
    }

    public int[] readInts(int max) {
        int n = readVarInt();
        if (n < 0 || n > max) {
            throw new IllegalStateException("Bad array length " + n);
        }
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = readVarInt();
        }
        return values;
    }
}
