package de.klotzi111.ktig.impl.keybinding;

import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

/**
 * Implementation note: When adding new methods to this interface make sure to also proxy these methods in {@link ProxyKeyBindingManager}
 */
public abstract class KeyBindingManager {

	/**
	 * The most upper {@link KeyBindingManager} in the chain of proxies
	 * this value is set after construction
	 */
	public KeyBindingManager upperDelegate;

	public KeyBindingManager() {
	}

	public void onModInit() {

	}

	public abstract String getMixinPackage();

	public abstract Set<String> getAdditionalMixinClassPrefixes();

	public abstract List<VersionedMixinClassGroup> getVersionedMixinClassGroups();

	public abstract Key getKeyFromKeyboard(int keycode, int scancode);

	public abstract Key getKeyFromMouse(int mouseButton);

	public abstract void incrementPressCount(KeyBinding keyBinding);

	public abstract void setPressed(KeyBinding keyBinding, boolean pressed);

	public abstract ObjectLinkedOpenCustomHashSet<KeyBinding> getKeyBindingsForKey(Key key);

	public abstract boolean doesKeyBindingMatchTriggerPoint(int triggerPoint, KeyBinding keyBinding, ObjectOpenCustomHashSet<KeyBinding> triggerPointRegisteredSet);

	/**
	 * This is the raw trigger event that is called from the KeyBoard and Mouse classes
	 *
	 * @param triggerPoint
	 * @param window
	 * @param key
	 * @param scancode
	 * @param action
	 * @param modifiers
	 * @param cancellable
	 * @return whether the event should be cancelled
	 */
	public abstract boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable);

	/**
	 * This method does the default behavior when a keyBinding is pressed or released
	 *
	 * @param keyBinding the keyBinding triggered
	 * @param triggerPoint
	 * @param action what happened with the key. See GLFW constants: {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE}, {@link GLFW#GLFW_REPEAT}
	 * @param key the key that was pressed, resolved from the mapping table with the raw key event data. This is useful when having an other mod that enables multiple keys on one keybinding nmuk for example
	 * @param keyConsumed whether the key matching this keybinding was already consumed by an other keybinding
	 * @return whether the event should be canceled. If an event is cancelled after all the keyBindings of the current trigger point has been processed, the event will NOT reach further trigger points
	 */
	public abstract boolean onTriggerDefaultKeyBinding(KeyBinding keyBinding, int triggerPoint, int action, Key key, boolean keyConsumed);

	public abstract void actionDefaultPress(KeyBinding keyBinding);

	public abstract void actionDefaultRelease(KeyBinding keyBinding);

}
