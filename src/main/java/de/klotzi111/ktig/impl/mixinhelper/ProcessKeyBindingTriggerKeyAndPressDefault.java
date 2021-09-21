package de.klotzi111.ktig.impl.mixinhelper;

import org.lwjgl.glfw.GLFW;

import de.klotzi111.ktig.impl.KTIGHelper;
import net.minecraft.client.util.InputUtil.Key;

public class ProcessKeyBindingTriggerKeyAndPressDefault implements ProcessKeyBindingTriggerKeyAndPress {
	public final long window;
	public final int triggerPoint;

	public ProcessKeyBindingTriggerKeyAndPressDefault(long window, int triggerPoint) {
		this.window = window;
		this.triggerPoint = triggerPoint;
	}

	@Override
	public boolean processKeyBindingTrigger(Key key, boolean pressed) {
		return KTIGHelper.processKeyBindingTrigger(triggerPoint, window, key, pressed ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, 0, true);
	}
}
