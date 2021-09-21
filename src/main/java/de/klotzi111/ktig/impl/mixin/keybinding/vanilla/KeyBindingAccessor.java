package de.klotzi111.ktig.impl.mixin.keybinding.vanilla;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {

	@Final
	@Accessor
	public static Map<InputUtil.Key, KeyBinding> getKEY_TO_BINDINGS() {
		return null;
	}

	@Accessor
	public Key getBoundKey();

	@Accessor
	int getTimesPressed();

	@Accessor
	void setTimesPressed(int timesPressed);
}
