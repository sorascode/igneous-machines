package com.herdlicka.igneousmachines;

import com.herdlicka.igneousmachines.gui.screen.ingame.DepositerScreen;
import com.herdlicka.igneousmachines.gui.screen.ingame.IgneousCrafterScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(value = EnvType.CLIENT)
public class IgneousMachinesModClient implements ClientModInitializer {

	static {
		HandledScreens.register(IgneousMachinesMod.IGNEOUS_CRAFTER_SCREEN_HANDLER, IgneousCrafterScreen::new);
		HandledScreens.register(IgneousMachinesMod.DEPOSITER_SCREEN_HANDLER, DepositerScreen::new);
	}

	@Override
	public void onInitializeClient() {
	}
}