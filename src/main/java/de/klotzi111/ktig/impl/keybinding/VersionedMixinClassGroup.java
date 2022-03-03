package de.klotzi111.ktig.impl.keybinding;

import java.util.Set;

public class VersionedMixinClassGroup {

	public final String mixinPackage;
	public final Set<String> versionedClassNames;

	public VersionedMixinClassGroup(String mixinPackage, Set<String> versionedClassNames) {
		this.mixinPackage = mixinPackage;
		this.versionedClassNames = versionedClassNames;
	}

}
