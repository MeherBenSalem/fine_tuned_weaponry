package com.naizo.finetuned;

import com.naizo.finetuned.procedures.*;

public final class FineTunedWeaponry {
	public static void init() {
		Constants.LOG.info("Initializing {}", Constants.MOD_NAME);
		CreateMainConfigProcedure.execute();
		CreateAxesConfigProcedure.execute();
		CreateFangConfigProcedure.execute();
		CreateHammerConfigProcedure.execute();
		CreateKatanConfigProcedure.execute();
		CreateScytheConfigProcedure.execute();
		CreateSwordsConfigProcedure.execute();
	}

	private FineTunedWeaponry() {
	}
}
