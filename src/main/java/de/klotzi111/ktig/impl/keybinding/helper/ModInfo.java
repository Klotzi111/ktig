package de.klotzi111.ktig.impl.keybinding.helper;

import java.util.Objects;
import java.util.Optional;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModInfo {
	public final String modName;
	public final ModVersionChecker versionChecker;

	public ModInfo(String modName, ModVersionChecker versionChecker) {
		this.modName = modName;
		this.versionChecker = versionChecker;
	}

	public Optional<ModContainer> getModContainer(FabricLoader loader) {
		return loader.getModContainer(modName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(modName, versionChecker);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModInfo other = (ModInfo) obj;
		return Objects.equals(modName, other.modName) && Objects.equals(versionChecker, other.versionChecker);
	}
}
