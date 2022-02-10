package de.klotzi111.ktig.impl.keybinding;

import java.util.Collection;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public abstract class ProxyKeyBindingManager extends KeyBindingManager {

	/**
	 * this value is set after construction
	 */
	public KeyBindingManager delegate;

	public ProxyKeyBindingManager() {
	}

	@Override
	public void onModInit() {
		delegate.onModInit();
	}

	@Override
	public String getMixinPackage() {
		return delegate.getMixinPackage();
	}

	@Override
	public Collection<String> getAdditionalMixinClassPrefixes() {
		return delegate.getAdditionalMixinClassPrefixes();
	}

	@Override
	public Key getKeyFromKeyboard(int keycode, int scancode) {
		return delegate.getKeyFromKeyboard(keycode, scancode);
	}

	@Override
	public Key getKeyFromMouse(int mouseButton) {
		return delegate.getKeyFromMouse(mouseButton);
	}

	@Override
	public void incrementPressCount(KeyBinding keyBinding) {
		delegate.incrementPressCount(keyBinding);
	}

	@Override
	public void setPressed(KeyBinding keyBinding, boolean pressed) {
		delegate.setPressed(keyBinding, pressed);
	}

	@Override
	public ObjectLinkedOpenCustomHashSet<KeyBinding> getKeyBindingsForKey(Key key) {
		return delegate.getKeyBindingsForKey(key);
	}

	@Override
	public boolean doesKeyBindingMatchTriggerPoint(int triggerPoint, KeyBinding keyBinding, ObjectOpenCustomHashSet<KeyBinding> triggerPointRegisteredSet) {
		return delegate.doesKeyBindingMatchTriggerPoint(triggerPoint, keyBinding, triggerPointRegisteredSet);
	}

	@Override
	public boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable) {
		return delegate.processKeyBindingTrigger(triggerPoint, window, key, action, modifiers, cancellable);
	}

	@Override
	public boolean onTriggerDefaultKeyBinding(KeyBinding keyBinding, int triggerPoint, int action, Key key, boolean keyConsumed) {
		return delegate.onTriggerDefaultKeyBinding(keyBinding, triggerPoint, action, key, keyConsumed);
	}

	@Override
	public void actionDefaultPress(KeyBinding keyBinding) {
		delegate.actionDefaultPress(keyBinding);
	}

	@Override
	public void actionDefaultRelease(KeyBinding keyBinding) {
		delegate.actionDefaultRelease(keyBinding);
	}

}
