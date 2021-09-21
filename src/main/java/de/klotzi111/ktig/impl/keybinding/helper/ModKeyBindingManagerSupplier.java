package de.klotzi111.ktig.impl.keybinding.helper;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import de.klotzi111.ktig.impl.keybinding.KeyBindingManager;

public class ModKeyBindingManagerSupplier {
	public final ModInfo modInfo;
	public final Supplier<KeyBindingManager> keyBindingManagerSupplier;
	public final Set<ModInfo> incompatibleMods;

	public ModKeyBindingManagerSupplier(ModInfo modInfo, Supplier<KeyBindingManager> keyBindingManagerSupplier, Set<ModInfo> incompatibleMods) {
		this.modInfo = modInfo;
		this.keyBindingManagerSupplier = keyBindingManagerSupplier;
		if (incompatibleMods == null) {
			incompatibleMods = Collections.emptySet();
		}
		this.incompatibleMods = incompatibleMods;
	}

	public boolean isModCompatible(ModInfo modInfo) {
		return !incompatibleMods.contains(modInfo);
	}

}
