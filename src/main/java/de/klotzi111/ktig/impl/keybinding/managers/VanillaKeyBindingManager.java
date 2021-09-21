package de.klotzi111.ktig.impl.keybinding.managers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.klotzi111.ktig.impl.keybinding.AbstractKeyBindingManager;
import de.klotzi111.ktig.impl.mixin.keybinding.vanilla.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class VanillaKeyBindingManager extends AbstractKeyBindingManager {

	public static final String mixinPackage = getMixinPackage("keybinding.vanilla");

	protected VanillaKeyBindingManager(String mixinPackage, Set<String> additionalMixinClasses) {
		super(mixinPackage, additionalMixinClasses);
	}

	public VanillaKeyBindingManager() {
		super(mixinPackage, null);
	}

	@Override
	public Key getKeyFromKeyboard(int keycode, int scancode) {
		return InputUtil.fromKeyCode(keycode, scancode);
	}

	@Override
	public Key getKeyFromMouse(int mouseButton) {
		return InputUtil.Type.MOUSE.createFromCode(mouseButton);
	}

	@Override
	public List<KeyBinding> getKeyBindingsForKey(Key key) {
		KeyBinding keyBinding = KeyBindingAccessor.getKEY_TO_BINDINGS().get(key);
		if (keyBinding == null) {
			return Collections.emptyList();
		}
		return Collections.singletonList(keyBinding);
	}

	@Override
	public void incrementPressCount(KeyBinding keyBinding) {
		KeyBindingAccessor kba = (KeyBindingAccessor) keyBinding;
		kba.setTimesPressed(kba.getTimesPressed() + 1);
	}

	@Override
	public void setPressed(KeyBinding keyBinding, boolean pressed) {
		keyBinding.setPressed(pressed);
	}
}
