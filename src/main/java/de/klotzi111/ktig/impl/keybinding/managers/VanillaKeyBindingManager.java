package de.klotzi111.ktig.impl.keybinding.managers;

import java.util.Set;

import org.lwjgl.glfw.GLFW;

import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.keybinding.BaseKeyBindingManager;
import de.klotzi111.ktig.impl.mixin.keybinding.vanilla.KeyBindingAccessor;
import de.klotzi111.ktig.impl.util.IdentityHashStrategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class VanillaKeyBindingManager extends BaseKeyBindingManager {

	public static final String mixinPackage = getMixinPackage("keybinding.vanilla");

	protected VanillaKeyBindingManager(String mixinPackage, Set<String> additionalMixinClasses) {
		super(mixinPackage, additionalMixinClasses);
	}

	public VanillaKeyBindingManager() {
		this(mixinPackage, null);
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
	public ObjectLinkedOpenCustomHashSet<KeyBinding> getKeyBindingsForKey(Key key) {
		KeyBinding keyBinding = KeyBindingAccessor.getKEY_TO_BINDINGS().get(key);
		ObjectLinkedOpenCustomHashSet<KeyBinding> ret = new ObjectLinkedOpenCustomHashSet<>(keyBinding == null ? 0 : 1, IdentityHashStrategy.IDENTITY_HASH_STRATEGY);
		if (keyBinding != null) {
			ret.add(keyBinding);
		}
		return ret;
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

	@Override
	public boolean doesKeyBindingMatchTriggerPoint(int triggerPoint, KeyBinding keyBinding, ObjectOpenCustomHashSet<KeyBinding> triggerPointRegisteredSet) {
		// if the triggerPoint is vanilla the check in the map is inverted. Meaning if there is a keybinding registered for NO_VANILLA_BIT it will NOT be triggered when VANILLA_BIT arrives
		boolean isVanilla = triggerPoint == KeyBindingTriggerPoints.VANILLA_BIT;
		boolean isSetNullOrEmpty = triggerPointRegisteredSet == null || triggerPointRegisteredSet.isEmpty();
		boolean isKBContainedInSet = false;

		isKBContainedInSet = isSetNullOrEmpty ? false : triggerPointRegisteredSet.contains(keyBinding);

		return isVanilla ? !isKBContainedInSet : isKBContainedInSet;
	}

	@Override
	public boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable) {
		return false;
	}

	@Override
	public boolean onTriggerDefaultKeyBinding(KeyBinding keyBinding, int triggerPoint, int action, Key key, boolean keyConsumed) {
		if (keyBinding != null) {
			if (action == GLFW.GLFW_RELEASE) {
				upperDelegate.actionDefaultRelease(keyBinding);
			} else {
				// PRESS or REPEAT
				upperDelegate.actionDefaultPress(keyBinding);
			}
		}
		return false;
	}

	@Override
	public void actionDefaultPress(KeyBinding keyBinding) {
		upperDelegate.setPressed(keyBinding, true);
		upperDelegate.incrementPressCount(keyBinding);
	}

	@Override
	public void actionDefaultRelease(KeyBinding keyBinding) {
		upperDelegate.setPressed(keyBinding, false);
	}

}
