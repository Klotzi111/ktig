package de.klotzi111.ktig.impl;

import java.util.Collection;
import java.util.stream.Stream;

import de.klotzi111.ktig.api.KTIG;
import de.klotzi111.ktig.api.KeyBindingTriggerEventListener;
import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import de.klotzi111.ktig.impl.util.IdentityHashStrategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
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
		boolean cancelFromKBM = KeyBindingManagerLoader.INSTANCE.processKeyBindingTrigger(triggerPoint, window, key, action, modifiers, cancellable);
		if (cancelFromKBM) {
			return true;
		}

		KTIG.CURRENT_EVENT_KEY_CONSUMED = false;
		// creating a array every time seems inperformant and unnecessary but this way it is thread-safe without the overhead of a threadlocal
		boolean[] cancelled = new boolean[] {false};

		ObjectLinkedOpenCustomHashSet<KeyBinding> keyBindings = KeyBindingManagerLoader.INSTANCE.getKeyBindingsForKey(key);
		Stream<KeyBinding> keyBindingStream = streamKeyBindingsMatchingTriggerPoint(triggerPoint, keyBindings);

		keyBindingStream.forEach(kb -> {
			cancelled[0] = triggerKeyBinding(kb, triggerPoint, action, key, cancelled[0]);
			if (!cancellable) {
				cancelled[0] = false;
			}
		});
		return cancelled[0];
	}

	public static Stream<KeyBinding> streamKeyBindingsMatchingTriggerPoint(int triggerPoint, Collection<KeyBinding> keyBindings) {
		ObjectOpenCustomHashSet<KeyBinding> set = KTIG.TRIGGERPOINT_KEYBINDINGS.get(triggerPoint);

		Stream<KeyBinding> keyBindingStream = keyBindings.stream().filter(kb -> KeyBindingManagerLoader.INSTANCE.doesKeyBindingMatchTriggerPoint(triggerPoint, kb, set));

		return keyBindingStream;
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
		// cancelledRet might be false and this will un-cancel the event. But that is intended
		KTIG.CURRENT_EVENT_KEY_CONSUMED = true;
		return cancelledRet;
	}

}
