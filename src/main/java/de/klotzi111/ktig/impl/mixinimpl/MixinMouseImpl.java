package de.klotzi111.ktig.impl.mixinimpl;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import de.klotzi111.fabricmultiversionhelper.api.mapping.MappingHelper;
import de.klotzi111.fabricmultiversionhelper.api.version.MinecraftVersionHelper;
import de.klotzi111.ktig.impl.KTIGMod;
import de.klotzi111.ktig.impl.duck.IMouse;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPress;
import de.siphalor.amecs.api.KeyBindingUtils;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class MixinMouseImpl {

	private static final Field GameOptions_discreteMouseScroll;
	private static final Field GameOptions_mouseWheelSensitivity;

	static {
		if (!MinecraftVersionHelper.isMCVersionAtLeast("1.19")) {
			GameOptions_discreteMouseScroll = MappingHelper.mapAndGetField(GameOptions.class, "field_19244", boolean.class);
			GameOptions_mouseWheelSensitivity = MappingHelper.mapAndGetField(GameOptions.class, "field_1889", double.class);
		} else {
			GameOptions_discreteMouseScroll = null;
			GameOptions_mouseWheelSensitivity = null;
		}
	}

	public static boolean onScrollReceived(IMouse _this, int triggerPoint, double deltaY, ProcessKeyBindingTriggerKeyAndPress triggerFunction, boolean manualDeltaWheel, int g) {
		Int2DoubleOpenHashMap TRIGGERPOINT_EVENTDELTAWHEEL = _this.ktig$getTRIGGERPOINT_EVENTDELTAWHEEL();

		int scrollCount = 0;
		if (manualDeltaWheel) {
			// from minecraft but patched
			// this code might be wrong when the vanilla mc code changes
			double eventDeltaWheel = TRIGGERPOINT_EVENTDELTAWHEEL.getOrDefault(triggerPoint, 0);
			if (eventDeltaWheel != 0.0D && Math.signum(deltaY) != Math.signum(eventDeltaWheel)) {
				eventDeltaWheel = 0.0D;
			}

			eventDeltaWheel += deltaY;
			scrollCount = (int) eventDeltaWheel;
			if (scrollCount == 0) {
				TRIGGERPOINT_EVENTDELTAWHEEL.put(triggerPoint, eventDeltaWheel);
				return false;
			}

			eventDeltaWheel -= scrollCount;
			TRIGGERPOINT_EVENTDELTAWHEEL.put(triggerPoint, eventDeltaWheel);
			// -from minecraft
		} else {
			scrollCount = g;
		}

		InputUtil.Key keyCode = KeyBindingUtils.getKeyFromScroll(scrollCount);

		scrollCount = Math.abs(scrollCount);

		int scrollCountCopy = scrollCount;
		int cancelledPresses = 0;
		while (scrollCount > 0) {
			cancelledPresses += triggerFunction.processKeyBindingTrigger(keyCode, true) ? 1 : 0;
			triggerFunction.processKeyBindingTrigger(keyCode, false);
			scrollCount--;
		}

		_this.ktig$setScrollPressedCancelled(cancelledPresses);
		return cancelledPresses == scrollCountCopy;
	}

	public static double calculateDeltaY(MinecraftClient client, double vertical) {
		// this is the line calculating the deltaY from Minecraft Mouse
		// this line must be updated if it changes in the MC code
		boolean discreteMouseScroll = false;
		double mouseWheelSensitivity = 0D;
		if (MinecraftVersionHelper.isMCVersionAtLeast("1.19")) {
			discreteMouseScroll = client.options.getDiscreteMouseScroll().getValue() != false;
			mouseWheelSensitivity = client.options.getMouseWheelSensitivity().getValue();
		} else {
			try {
				discreteMouseScroll = GameOptions_discreteMouseScroll.getBoolean(client.options);
				mouseWheelSensitivity = GameOptions_mouseWheelSensitivity.getDouble(client.options);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				KTIGMod.log(Level.ERROR, "Failed to get fields \"discreteMouseScroll\" and \"mouseWheelSensitivity\"");
				KTIGMod.logException(Level.ERROR, e);
			}
		}

		double deltaY = (discreteMouseScroll ? Math.signum(vertical) : vertical) * mouseWheelSensitivity;
		return deltaY;
	}
}
