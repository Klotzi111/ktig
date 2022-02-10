package de.klotzi111.ktig.impl.keybinding;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import de.klotzi111.ktig.impl.KTIGMod;
import de.klotzi111.ktig.impl.keybinding.helper.ModInfo;
import de.klotzi111.ktig.impl.util.IdentityHashStrategy;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class MultipleKeyBindingManagerManager extends KeyBindingManager {

	public final Map<ModInfo, KeyBindingManager> keyBindingManagers;

	public MultipleKeyBindingManagerManager(Map<ModInfo, KeyBindingManager> keyBindingManagers) {
		this.keyBindingManagers = keyBindingManagers;
	}

	private void doForEachKBM(Consumer<KeyBindingManager> consumer) {
		keyBindingManagers.values().stream().forEach(consumer);
	}

	private void doForFirstKBM(Consumer<KeyBindingManager> consumer) {
		consumer.accept(keyBindingManagers.values().iterator().next());
	}

	private <R> R doForFirstKBMRet(Function<KeyBindingManager, R> function) {
		return function.apply(keyBindingManagers.values().iterator().next());
	}

	@Override
	public String getMixinPackage() {
		// we have no mixin package
		return null;
	}

	@Override
	public Collection<String> getAdditionalMixinClassPrefixes() {
		Set<String> prefixes = new HashSet<>();
		doForEachKBM((KBM) -> {
			prefixes.add(KBM.getMixinPackage());
			prefixes.addAll(KBM.getAdditionalMixinClassPrefixes());
		});
		return prefixes;
	}

	private Key getMostReportedKey(Function<KeyBindingManager, Key> function, Supplier<String> errorMessageSupplier) {
		Object2IntArrayMap<Key> keys = new Object2IntArrayMap<>();
		doForEachKBM((KBM) -> {
			Key key = function.apply(KBM);
			keys.put(key, keys.getOrDefault(key, 0) + 1);
		});
		Optional<Entry<Key>> maxOpt = keys.object2IntEntrySet().stream().max((a, b) -> Integer.compare(a.getIntValue(), b.getIntValue()));
		if (maxOpt.isPresent()) {
			return maxOpt.get().getKey();
		} else {
			KTIGMod.log(Level.ERROR, "Managers did not found any suitable key for: " + errorMessageSupplier.get());
			return InputUtil.UNKNOWN_KEY;
		}
	}

	@Override
	public Key getKeyFromKeyboard(int keycode, int scancode) {
		return getMostReportedKey((KBM) -> KBM.getKeyFromKeyboard(keycode, scancode), () -> "keycode=" + keycode + ", scancode=" + scancode);
	}

	@Override
	public Key getKeyFromMouse(int mouseButton) {
		return getMostReportedKey((KBM) -> KBM.getKeyFromMouse(mouseButton), () -> "mouseButton=" + mouseButton);
	}

	@Override
	public void incrementPressCount(KeyBinding keyBinding) {
		doForFirstKBM((KBM) -> KBM.incrementPressCount(keyBinding));
	}

	@Override
	public void setPressed(KeyBinding keyBinding, boolean pressed) {
		doForFirstKBM((KBM) -> KBM.setPressed(keyBinding, pressed));
	}

	@Override
	public ObjectLinkedOpenCustomHashSet<KeyBinding> getKeyBindingsForKey(Key key) {
		ObjectLinkedOpenCustomHashSet<KeyBinding> keyBindings = new ObjectLinkedOpenCustomHashSet<>(IdentityHashStrategy.IDENTITY_HASH_STRATEGY);
		doForEachKBM((KBM) -> keyBindings.addAll(KBM.getKeyBindingsForKey(key)));
		return keyBindings;
	}

	@Override
	public boolean doesKeyBindingMatchTriggerPoint(int triggerPoint, KeyBinding keyBinding, ObjectOpenCustomHashSet<KeyBinding> triggerPointRegisteredSet) {
		return doForFirstKBMRet((KBM) -> KBM.doesKeyBindingMatchTriggerPoint(triggerPoint, keyBinding, triggerPointRegisteredSet));
	}

	@Override
	public boolean processKeyBindingTrigger(int triggerPoint, long window, Key key, int action, int modifiers, boolean cancellable) {
		return doForFirstKBMRet((KBM) -> KBM.processKeyBindingTrigger(triggerPoint, window, key, action, modifiers, cancellable));
	}

	@Override
	public boolean onTriggerDefaultKeyBinding(KeyBinding keyBinding, int triggerPoint, int action, Key key, boolean keyConsumed) {
		return doForFirstKBMRet((KBM) -> KBM.onTriggerDefaultKeyBinding(keyBinding, triggerPoint, action, key, keyConsumed));
	}

	@Override
	public void actionDefaultPress(KeyBinding keyBinding) {
		doForFirstKBM((KBM) -> KBM.actionDefaultPress(keyBinding));
	}

	@Override
	public void actionDefaultRelease(KeyBinding keyBinding) {
		doForFirstKBM((KBM) -> KBM.actionDefaultRelease(keyBinding));
	}

}
