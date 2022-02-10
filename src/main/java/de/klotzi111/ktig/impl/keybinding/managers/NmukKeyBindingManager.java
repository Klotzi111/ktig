package de.klotzi111.ktig.impl.keybinding.managers;

import de.klotzi111.ktig.impl.keybinding.ProxyKeyBindingManager;
import de.siphalor.nmuk.api.NMUKAlternatives;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;

public class NmukKeyBindingManager extends ProxyKeyBindingManager {

	public NmukKeyBindingManager() {
	}

	@Override
	public boolean doesKeyBindingMatchTriggerPoint(int triggerPoint, KeyBinding keyBinding, ObjectOpenCustomHashSet<KeyBinding> triggerPointRegisteredSet) {
		KeyBinding base = NMUKAlternatives.getBase(keyBinding);
		if (base != null) {
			// if the keyBinding has a parent
			// this keyBinding is ONLY allowed if the base is allowed
			return super.doesKeyBindingMatchTriggerPoint(triggerPoint, base, triggerPointRegisteredSet);
		}
		return super.doesKeyBindingMatchTriggerPoint(triggerPoint, keyBinding, triggerPointRegisteredSet);
	}

	@Override
	public void incrementPressCount(KeyBinding keyBinding) {
		super.incrementPressCount(keyBinding);

		KeyBinding base = NMUKAlternatives.getBase(keyBinding);
		if (base != null) {
			// if the keyBinding has a parent
			// also increase the parent's press count
			super.incrementPressCount(base);
		}
	}

}
