package de.klotzi111.ktig.impl.keybinding;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public interface KeyBindingManager {

	public default void onModInit() {

	}

	public String getMixinPackage();

	public Collection<String> getAdditionalMixinClassPrefixes();

	public Key getKeyFromKeyboard(int keycode, int scancode);

	public Key getKeyFromMouse(int mouseButton);

	public void incrementPressCount(KeyBinding keyBinding);

	public void setPressed(KeyBinding keyBinding, boolean pressed);

	public List<KeyBinding> getKeyBindingsForKey(Key key);

}
