package com.naizo.finetuned.neoforge.platform;

import com.naizo.finetuned.Constants;
import com.naizo.finetuned.network.PlayerVariables;
import com.naizo.finetuned.platform.Services;
import com.naizo.finetuned.platform.services.IPlayerVariablesAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgePlayerVariables implements IPlayerVariablesAccess {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
			DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Constants.MOD_ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerVariables>> PLAYER_VARIABLES =
			ATTACHMENTS.register("player_variables", () -> AttachmentType.builder(() -> new PlayerVariables())
					.serialize(new IAttachmentSerializer<CompoundTag, PlayerVariables>() {
						@Override
						public PlayerVariables read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							PlayerVariables variables = new PlayerVariables();
							variables.readNBT(tag);
							return variables;
						}

						@Override
						public CompoundTag write(PlayerVariables attachment, HolderLookup.Provider provider) {
							return (CompoundTag) attachment.writeNBT();
						}
					})
					.copyOnDeath()
					.build());

	public static void register(IEventBus modBus) {
		ATTACHMENTS.register(modBus);
	}

	@Override
	public PlayerVariables get(Player player) {
		return player.getData(PLAYER_VARIABLES);
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
	public static void onClone(PlayerEvent.Clone event) {
		((NeoForgePlayerVariables) Services.PLAYER_VARIABLES).copyOnClone(event.getOriginal(), event.getEntity(), event.isWasDeath());
		if (!event.getEntity().level().isClientSide()) {
			((NeoForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((NeoForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((NeoForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			((NeoForgePlayerVariables) Services.PLAYER_VARIABLES).sync(event.getEntity());
		}
	}
}
