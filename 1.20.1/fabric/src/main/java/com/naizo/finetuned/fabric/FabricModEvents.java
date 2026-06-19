package com.naizo.finetuned.fabric;

import com.naizo.finetuned.procedures.*;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class FabricModEvents {
	private FabricModEvents() {
	}

	public static void register() {
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			Entity attacker = source.getEntity();
			if (attacker != null) {
				AttackGemTriggersProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, attacker, amount);
			}
			return true;
		});
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			Entity attacker = source.getEntity();
			InfernoCoreDiesProcedure.execute(entity.level(), entity, attacker);
			OnMobDeathProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Player player : server.getPlayerList().getPlayers()) {
				MoonsLunarBloomFangPassiveProcedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player, player);
			}
		});
	}
}
