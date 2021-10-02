package de.klotzi111.ktig.api;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil.Key;

public interface KeyBindingTriggerEventListener {

	/**
	 *
	 * @return whether the {@link #onTrigger} method should be called when a trigger event is already cancelled
	 */
	public default boolean ignoreCancelled() {
		return false;
	}

	/**
	 *
	 * @return whether the {@link #onTrigger} method should be called when the key matching this keybinding was already consumed by an other keybinding
	 */
	public default boolean ignoreKeyConsumed() {
		return true;
	}

	/**
	 * This method is called when the keybinding is triggered.
	 * <br>
	 * This method can 'uncancel' a trigger event.
	 *
	 * @param triggerPoint
	 * @param action what happened with the key. See GLFW constants: {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE}, {@link GLFW#GLFW_REPEAT}
	 * @param key the key that was pressed, resolved from the mapping table with the raw key event data. This is useful when having an other mod that enables multiple keys on one keybinding nmuk for example
	 * @param keyConsumed whether the key matching this keybinding was already consumed by an other keybinding
	 * @param cancelled whether the event is currently cancelled
	 * @return whether the event should be canceled. If an event is cancelled after all the keyBindings of the current trigger point has been processed, the event will NOT reach further trigger points
	 */
	public default boolean onTrigger(int triggerPoint, int action, Key key, boolean keyConsumed, boolean cancelled) {
		boolean cancel = onTrigger(triggerPoint, action, key, keyConsumed);
		return cancel || cancelled;
	}

	/**
	 * This method is called when the keybinding is triggered.
	 * <br>
	 * See {@link #onTrigger(int, int, Key, boolean, boolean)} for more control over whether the event should stay cancelled.
	 *
	 * @see #onTrigger(int, int, Key, boolean, boolean)
	 *
	 * @param triggerPoint
	 * @param action what happened with the key. See GLFW constants: {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE}, {@link GLFW#GLFW_REPEAT}
	 * @param key the key that was pressed, resolved from the mapping table with the raw key event data. This is useful when having an other mod that enables multiple keys on one keybinding nmuk for example
	 * @param keyConsumed whether the key matching this keybinding was already consumed by an other keybinding
	 * @return whether the event should be canceled. If an event is cancelled after all the keyBindings of the current trigger point has been processed, the event will NOT reach further trigger points
	 */
	public boolean onTrigger(int triggerPoint, int action, Key key, boolean keyConsumed);
}
