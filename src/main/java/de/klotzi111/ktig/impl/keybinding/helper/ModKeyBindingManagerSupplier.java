package de.klotzi111.ktig.impl.keybinding.helper;

import java.util.Collections;
import java.util.Set;

import de.klotzi111.ktig.impl.keybinding.KeyBindingManager;

public class ModKeyBindingManagerSupplier {

	@FunctionalInterface
	public static interface KeyBindingManagerFactory {
		public KeyBindingManager createKeyBindingManager();
	}

	public final ModInfo modInfo;
	public final KeyBindingManagerFactory keyBindingManagerFactory;
	public final Set<ModInfo> incompatibleMods;

	public ModKeyBindingManagerSupplier(ModInfo modInfo, KeyBindingManagerFactory keyBindingManagerFactory, Set<ModInfo> incompatibleMods) {
		this.modInfo = modInfo;
		this.keyBindingManagerFactory = keyBindingManagerFactory;
		if (incompatibleMods == null) {
			incompatibleMods = Collections.emptySet();
		}
		this.incompatibleMods = incompatibleMods;
	}

	public boolean isModCompatible(ModInfo modInfo) {
		return !incompatibleMods.contains(modInfo);
	}

}
