package com.naizo.finetuned.forge.platform;

import com.naizo.finetuned.network.PlayerVariables;
import com.naizo.finetuned.platform.services.IPlayerVariablesAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.naizo.finetuned.Constants;
import com.naizo.finetuned.platform.Services;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgePlayerVariables implements IPlayerVariablesAccess {
	public static final Capability<PlayerVariables> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	@Override
	public PlayerVariables get(Player player) {
		return player.getCapability(CAPABILITY).orElse(new PlayerVariables());
	}

	@Override
	public void sync(Player player) {
		get(player).sync(player);
	}

	@Override
	public void copyOnClone(Player original, Player clone, boolean wasDeath) {
		PlayerVariables oldVars = get(original);
		PlayerVariables newVars = get(clone);
		newVars.page = oldVars.page;
	}

	@SubscribeEvent
	public static void attachCapabilities(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
		if (event.getObject() instanceof Player player && !(player instanceof net.minecraftforge.common.util.FakePlayer)) {
			event.addCapability(new net.minecraft.resources.ResourceLocation(Constants.MOD_ID, "player_variables"), new Provider());
		}
	}

	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event) {
		((ForgePlayerVariables) Services.PLAYER_VARIABLES).copyOnClone(event.getOriginal(), event.getEntity(), event.isWasDeath());
		if (!event.getEntity().level().isClientSide()) {
			((ForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((ForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((ForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((ForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	private static class Provider implements ICapabilitySerializable<CompoundTag> {
		private final PlayerVariables variables = new PlayerVariables();
		private final LazyOptional<PlayerVariables> optional = LazyOptional.of(() -> variables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, net.minecraft.core.Direction side) {
			return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			return (CompoundTag) variables.writeNBT();
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			variables.readNBT(nbt);
		}
	}
}
