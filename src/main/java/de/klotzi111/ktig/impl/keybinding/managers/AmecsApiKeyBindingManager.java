package de.klotzi111.ktig.impl.keybinding.managers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import de.siphalor.amecs.impl.AmecsAPI;
import de.siphalor.amecs.impl.KeyBindingManager;
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
	public List<KeyBinding> getKeyBindingsForKey(Key key) {
		Stream<KeyBinding> stream = Streams.concat(KeyBindingManager.getMatchingKeyBindings(key, true), KeyBindingManager.getMatchingKeyBindings(key, false));
		return stream.collect(Collectors.toList());
	}
}
