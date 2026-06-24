package com.naizo.finetuned.fabric.network;

import com.naizo.finetuned.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FabricDynamicPayload(FriendlyByteBuf data) implements CustomPacketPayload {
	public static final Type<FabricDynamicPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "dynamic"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FabricDynamicPayload> STREAM_CODEC = StreamCodec.of(
			(buf, payload) -> buf.writeBytes(payload.data()),
			buf -> new FabricDynamicPayload(new FriendlyByteBuf(buf.readBytes(buf.readableBytes())))
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
