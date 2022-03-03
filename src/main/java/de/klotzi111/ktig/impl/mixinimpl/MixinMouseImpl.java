package de.klotzi111.ktig.impl.mixinimpl;

import de.klotzi111.ktig.impl.duck.IMouse;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPress;
import de.siphalor.amecs.api.KeyBindingUtils;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class MixinMouseImpl {

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

}
