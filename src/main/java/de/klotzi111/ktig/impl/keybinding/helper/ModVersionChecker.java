package de.klotzi111.ktig.impl.keybinding.helper;

import net.fabricmc.loader.api.Version;

@FunctionalInterface
public interface ModVersionChecker {
	public boolean isVersionSupported(Version modVersion);

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);
}
