package de.klotzi111.ktig.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KTIGMod implements ClientModInitializer {

	public static final String MOD_ID = "ktig";
	public static final String MOD_NAME = "KTIG";

	private static final String LOG_PREFIX = "[" + MOD_NAME + "] ";
	public static Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		KeyBindingManagerLoader.createKeyBindingManager();

		KeyBindingManagerLoader.INSTANCE.onModInit();
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, LOG_PREFIX + message);
	}

	public static void logException(Level level, Throwable e) {
		LOGGER.catching(level, e);
	}
}
