package de.klotzi111.ktig.impl.util;

import de.klotzi111.ktig.impl.keybinding.helper.ModVersionChecker;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;

@FunctionalInterface
public interface ModSemanticVersionChecker extends ModVersionChecker {
	@Override
	public default boolean isVersionSupported(Version modVersion) {
		if (modVersion instanceof SemanticVersion) {
			return isSemanticVersionSupported((SemanticVersion) modVersion);
		}
		return false;
	}

	public boolean isSemanticVersionSupported(SemanticVersion modVersion);
}
