package de.klotzi111.ktig.impl.keybinding.managers;

import java.util.ArrayList;
import java.util.List;

import de.klotzi111.ktig.impl.keybinding.ProxyKeyBindingManager;
import de.klotzi111.ktig.impl.util.IdentityHashStrategy;
import de.siphalor.nmuk.api.NMUKAlternatives;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public class NmukKeyBindingManager extends ProxyKeyBindingManager {

	public NmukKeyBindingManager() {
	}

	@Override
	public List<KeyBinding> getKeyBindingsForKey(Key key) {
		List<KeyBinding> list = super.getKeyBindingsForKey(key);
		ObjectOpenCustomHashSet<KeyBinding> extra = new ObjectOpenCustomHashSet<>(list, IdentityHashStrategy.IDENTITY_HASH_STRATEGY);
		list.stream().forEach((kb) -> {
			KeyBinding base = NMUKAlternatives.getBase(kb);
			if (base != null) {
				extra.add(base);
			}
		});
		return new ArrayList<>(extra);
	}
}
