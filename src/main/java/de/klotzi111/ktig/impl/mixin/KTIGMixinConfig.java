package de.klotzi111.ktig.impl.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KTIGMixinConfig implements IMixinConfigPlugin {

	private String packagePrefix = null;
	private Collection<String> additionalMixinClassPrefixes = null;

	@Override
	public void onLoad(String mixinPackage) {
		KeyBindingManagerLoader.createKeyBindingManager();
		packagePrefix = KeyBindingManagerLoader.INSTANCE.getMixinPackage();
		additionalMixinClassPrefixes = KeyBindingManagerLoader.INSTANCE.getAdditionalMixinClassPrefixes();
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if ((packagePrefix != null && mixinClassName.startsWith(packagePrefix)) || additionalMixinClassPrefixes.stream().anyMatch((prefix) -> mixinClassName.startsWith(prefix))) {
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
