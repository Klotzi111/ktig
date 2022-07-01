package de.klotzi111.ktig.impl.mixin;

import java.util.*;
import java.util.Map.Entry;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import de.klotzi111.fabricmultiversionhelper.api.version.MinecraftVersionHelper;
import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import de.klotzi111.ktig.impl.keybinding.VersionedMixinClassGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

@Environment(EnvType.CLIENT)
public class KTIGMixinConfig implements IMixinConfigPlugin {

	private static SemanticVersion getVersionForVersionedMixinClassName(String classNameOnly) {
		int indexOfVersionSep = classNameOnly.indexOf("_");
		if (indexOfVersionSep == -1) {
			return null;
		}
		String versionString = classNameOnly.substring(indexOfVersionSep + 1).replace("_", ".");
		try {
			return SemanticVersion.parse(versionString);
		} catch (VersionParsingException e) {
			throw new IllegalStateException("Could not parse version string for versioned mixin class: " + classNameOnly, e);
		}
	}

	private String packagePrefix = null;
	private Set<String> additionalMixinClassPrefixes = null;

	private Set<String> selectedVersionedClasses = new HashSet<>();
	private Set<String> allVersionedClasses = new HashSet<>();

	@Override
	public void onLoad(String mixinPackage) {
		KeyBindingManagerLoader.createKeyBindingManager();
		packagePrefix = KeyBindingManagerLoader.INSTANCE.getMixinPackage();

		Set<String> aMCP = KeyBindingManagerLoader.INSTANCE.getAdditionalMixinClassPrefixes();
		additionalMixinClassPrefixes = aMCP == null ? Collections.emptySet() : aMCP;

		// select versioned classes
		List<VersionedMixinClassGroup> versionedClassGroups = KeyBindingManagerLoader.INSTANCE.getVersionedMixinClassGroups();
		for (VersionedMixinClassGroup vmcg : versionedClassGroups) {
			TreeMap<SemanticVersion, String> mixinClassesWithVersion = new TreeMap<>();
			for (String vcn : vmcg.versionedClassNames) {
				SemanticVersion version = getVersionForVersionedMixinClassName(vcn);
				if (version != null) {
					mixinClassesWithVersion.put(version, vcn);
				}

				allVersionedClasses.add(vmcg.mixinPackage + "." + vcn);
			}

			Entry<SemanticVersion, String> suitable = mixinClassesWithVersion.floorEntry(MinecraftVersionHelper.SEMANTIC_MINECRAFT_VERSION);
			if (suitable != null) {
				selectedVersionedClasses.add(vmcg.mixinPackage + "." + suitable.getValue());
			}
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if ((packagePrefix != null && mixinClassName.startsWith(packagePrefix)) || additionalMixinClassPrefixes.stream().anyMatch((prefix) -> mixinClassName.startsWith(prefix))) {
			// if the mixin is allowed by the package prefixes

			// check if this mixin's package is a versioned class package
			if (allVersionedClasses.contains(mixinClassName)) {
				// if it is a versioned class package
				if (!selectedVersionedClasses.contains(mixinClassName)) {
					// if this is not the selected class name we disable it
					return false;
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
