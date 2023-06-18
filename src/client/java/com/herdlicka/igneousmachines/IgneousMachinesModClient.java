package com.herdlicka.igneousmachines;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class IgneousMachinesModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(IgneousMachinesMod.IGNEOUS_CRAFTER_SCREEN_HANDLER, IgneousCrafterScreen::new);
		HandledScreens.register(IgneousMachinesMod.DEPOSITER_SCREEN_HANDLER, DepositerScreen::new);
	}
}