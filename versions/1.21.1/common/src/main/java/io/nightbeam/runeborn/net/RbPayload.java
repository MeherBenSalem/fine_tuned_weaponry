package io.nightbeam.runeborn.net;

import io.nightbeam.runeborn.compat.Compat;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** The single Runeborn channel. Payload bytes are laid out by {@code core.net.Packets}. */
public record RbPayload(byte[] data) implements CustomPacketPayload {
    public static final Type<RbPayload> TYPE = new Type<>(Compat.id("main"));
    public static final StreamCodec<ByteBuf, RbPayload> CODEC = ByteBufCodecs.BYTE_ARRAY.map(RbPayload::new, RbPayload::data);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
