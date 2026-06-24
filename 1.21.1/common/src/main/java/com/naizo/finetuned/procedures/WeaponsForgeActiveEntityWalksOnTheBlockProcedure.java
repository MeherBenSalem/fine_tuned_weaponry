package com.naizo.finetuned.procedures;

import net.minecraft.world.entity.Entity;

public class WeaponsForgeActiveEntityWalksOnTheBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.igniteForSeconds(3);
	}
}
