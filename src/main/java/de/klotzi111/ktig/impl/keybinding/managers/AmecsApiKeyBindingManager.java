package de.klotzi111.ktig.impl.keybinding.managers;

import org.lwjgl.glfw.GLFW;

import de.klotzi111.ktig.impl.KTIGHelper;
import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.impl.KeyBindingManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public class AmecsApiKeyBindingManager extends VanillaKeyBindingManager {

	public static final String mixinPackage = getMixinPackage("keybinding.amecsapi");

	public AmecsApiKeyBindingManager() {
		super(mixinPackage, null);
		String vanillaMixinPackage = VanillaKeyBindingManager.mixinPackage;
		additionalMixinClassPrefixes.add(vanillaMixinPackage);
	}

	@Override
	public void onModInit() {
		AmecsAPI.TRIGGER_KEYBINDING_ON_SCROLL = false;
	}

	@Override
	public ObjectLinkedOpenCustomHashSet<KeyBinding> getKeyBindingsForKey(Key key) {
		return KeyBindingManager.getKeyBindingsWithKey(key);
	}

	@Override
	public boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable) {
		boolean pressed = action != GLFW.GLFW_RELEASE;
		KeyBindingManager.checkKeyBindingsWithJustReleasedKeyModifier(key, pressed, (kbs) -> KTIGHelper.streamKeyBindingsMatchingTriggerPoint(triggerPoint, kbs));

		return super.processKeyBindingTrigger(triggerPoint, window, key, action, modifiers, cancellable);
	}
}
