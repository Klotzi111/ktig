package de.klotzi111.ktig.impl;

import java.util.List;
import java.util.stream.Stream;

import de.klotzi111.ktig.api.KTIG;
import de.klotzi111.ktig.api.KeyBindingTriggerEventListener;
import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import de.klotzi111.ktig.impl.util.IdentityHashStrategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public class KTIGHelper {

	public static interface BitAcceptor {
		public boolean accept(int bit);
	}

	public static int doAndSumForAllBits(int bits, BitAcceptor acceptor) {
		int doneFor = 0;
		for (int i = 0; i < Integer.SIZE; i++) {
			int bitValue = (1 << i);
			if (KeyBindingTriggerPoints.areAllBitsSet(bits, bitValue)) {
				doneFor |= acceptor.accept(bitValue) ? bitValue : 0;
			}
		}
		return doneFor;
	}

	public static ObjectOpenCustomHashSet<KeyBinding> createKeyBindingHashSet() {
		return new ObjectOpenCustomHashSet<>(IdentityHashStrategy.IDENTITY_HASH_STRATEGY);
	}

	/**
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
	public static boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable) {
		// if the triggerPoint is vanilla the check in the map is inverted. Meaning if there is a keybinding registered for NO_VANILLA_BIT it will NOT be triggered when VANILLA_BIT arrives
		boolean isVanilla = triggerPoint == KeyBindingTriggerPoints.VANILLA_BIT;
		ObjectOpenCustomHashSet<KeyBinding> set = KTIG.TRIGGERPOINT_KEYBINDINGS.get(triggerPoint);
		if (!isVanilla && (set == null || set.isEmpty())) {
			return false;
		}

		boolean[] cancelled = new boolean[] {false};
		List<KeyBinding> keyBindings = KeyBindingManagerLoader.INSTANCE.getKeyBindingsForKey(key);
		Stream<KeyBinding> keyBindingStream = keyBindings.stream();
		if (!isVanilla) {
			keyBindingStream = keyBindingStream.filter(kb -> set.contains(kb));
		} else {
			if (!(set == null || set.isEmpty())) {
				keyBindingStream = keyBindingStream.filter(kb -> !set.contains(kb));
			}
		}
		keyBindingStream.forEach(kb -> {
			cancelled[0] = triggerKeyBinding(kb, triggerPoint, action, key, cancelled[0]);
			if (!cancellable) {
				cancelled[0] = false;
			}
		});
		return cancelled[0];
	}

	private static class DefaultKeyBindingTriggerEventListener implements KeyBindingTriggerEventListener {
		public KeyBinding currentKeyBinding = null;

		@Override
		public boolean onTrigger(int triggerPoint, int action, Key key, boolean keyConsumed) {
			return KeyBindingTriggerEventListener.onTriggerDefaultKeyBinding(currentKeyBinding, triggerPoint, action, key, keyConsumed);
		}
	}

	private static final DefaultKeyBindingTriggerEventListener DEFAULT_TRIGGER_EVENT_LISTENER = new DefaultKeyBindingTriggerEventListener();

	public static boolean triggerKeyBinding(KeyBinding keyBinding, int triggerPoint, int action, Key key, boolean cancelled) {
		DEFAULT_TRIGGER_EVENT_LISTENER.currentKeyBinding = keyBinding;
		KeyBindingTriggerEventListener eventListener = DEFAULT_TRIGGER_EVENT_LISTENER;
		// TODO: Advanced event listener
		if (keyBinding instanceof KeyBindingTriggerEventListener) {
			eventListener = (KeyBindingTriggerEventListener) keyBinding;
		}
		if (cancelled && !eventListener.ignoreCancelled()) {
			return cancelled;
		}
		if (KTIG.CURRENT_EVENT_KEY_CONSUMED && !eventListener.ignoreKeyConsumed()) {
			return cancelled;
		}

		boolean cancelledRet = eventListener.onTrigger(triggerPoint, action, key, KTIG.CURRENT_EVENT_KEY_CONSUMED, cancelled);
		KTIG.CURRENT_EVENT_KEY_CONSUMED = true;
		return cancelledRet;
	}

}
