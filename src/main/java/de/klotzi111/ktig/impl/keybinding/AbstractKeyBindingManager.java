package de.klotzi111.ktig.impl.keybinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.klotzi111.ktig.impl.KTIGMod;

public abstract class AbstractKeyBindingManager implements KeyBindingManager {

	protected final String mixinPackage;
	protected final Set<String> additionalMixinClassPrefixes;

	public AbstractKeyBindingManager(String mixinPackage, Set<String> additionalMixinClassPrefixes) {
		this.mixinPackage = mixinPackage;
		if (additionalMixinClassPrefixes == null) {
			additionalMixinClassPrefixes = new HashSet<>();
		}
		this.additionalMixinClassPrefixes = additionalMixinClassPrefixes;
	}

	@Override
	public String getMixinPackage() {
		return mixinPackage;
	}

	@Override
	public Collection<String> getAdditionalMixinClassPrefixes() {
		return additionalMixinClassPrefixes;
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
