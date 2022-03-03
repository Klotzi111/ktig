package de.klotzi111.ktig.impl.keybinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.klotzi111.ktig.impl.KTIGMod;

public abstract class BaseKeyBindingManager extends KeyBindingManager {

	protected final String mixinPackage;
	protected final Set<String> additionalMixinClassPrefixes = new HashSet<>();
	protected final List<VersionedMixinClassGroup> versionedMixinClassGroups = new ArrayList<>();

	public BaseKeyBindingManager(String mixinPackage) {
		this.mixinPackage = mixinPackage;
	}

	@Override
	public String getMixinPackage() {
		return mixinPackage;
	}

	@Override
	public Set<String> getAdditionalMixinClassPrefixes() {
		return additionalMixinClassPrefixes;
	}

	@Override
	public List<VersionedMixinClassGroup> getVersionedMixinClassGroups() {
		return versionedMixinClassGroups;
	}

	private static final String MIXIN_PACKAGE_NAME = "mixin";
	private static final String MOD_CLASS_PACKAGE;

	static {
		MOD_CLASS_PACKAGE = KTIGMod.class.getPackage().getName();
	}

	public static String getMixinPackage(String mixinSuffix) {
		return MOD_CLASS_PACKAGE + "." + MIXIN_PACKAGE_NAME + "." + mixinSuffix;
	}

}
