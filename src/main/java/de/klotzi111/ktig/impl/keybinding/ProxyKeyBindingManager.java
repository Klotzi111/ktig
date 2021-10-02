package de.klotzi111.ktig.impl.keybinding;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public abstract class ProxyKeyBindingManager implements KeyBindingManager {

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
	public List<KeyBinding> getKeyBindingsForKey(Key key) {
		return delegate.getKeyBindingsForKey(key);
	}

}
