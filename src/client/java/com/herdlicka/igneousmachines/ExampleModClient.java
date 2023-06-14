package com.herdlicka.igneousmachines;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ExampleMod.IGNEOUS_PLACER_SCREEN_HANDLER, IgneousPlacerScreen::new);
	}
}