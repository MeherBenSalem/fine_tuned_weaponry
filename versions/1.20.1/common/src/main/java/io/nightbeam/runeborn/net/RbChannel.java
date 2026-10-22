package io.nightbeam.runeborn.net;

import io.nightbeam.runeborn.compat.Compat;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * The single Runeborn channel. Payload bytes are laid out by {@code core.net.Packets} and framed as a
 * length-prefixed byte array, the same wire format as the payload codec on newer versions.
 */
public final class RbChannel {
    public static final ResourceLocation ID = Compat.id("main");

    public static FriendlyByteBuf encode(byte[] data) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(data.length + 5));
        buf.writeByteArray(data);
        return buf;
    }

    public static byte[] decode(FriendlyByteBuf buf) {
        return buf.readByteArray();
    }

    private RbChannel() {
    }
}
