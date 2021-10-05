package de.klotzi111.ktig.impl.mixin.keybinding.vanilla;

import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.KTIGHelper;
import de.klotzi111.ktig.impl.keybinding.KeyBindingManagerLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.util.InputUtil.Key;

@Environment(EnvType.CLIENT)
@Mixin(value = Keyboard.class, priority = 1050)
public class MixinKeyboard {

	// could also inject at head
	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", ordinal = 0), cancellable = true)
	private void injection_ALL_WINDOWS(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromKeyboard(key, scancode);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.ALL_WINDOWS_BIT, window, keyObject, action, modifiers, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "onKey", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), cancellable = true)
	private void injection_MAIN_WINDOW(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromKeyboard(key, scancode);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.MAIN_WINDOW_BIT, window, keyObject, action, modifiers, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "onKey", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 2), cancellable = true)
	private void injection_NON_SCREEN_PROCESSED(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromKeyboard(key, scancode);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.NON_SCREEN_PROCESSED_BIT, window, keyObject, action, modifiers, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "onKey", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;", ordinal = 0), cancellable = true)
	private void injection_NO_SCREEN(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromKeyboard(key, scancode);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.NO_SCREEN_BIT, window, keyObject, action, modifiers, true)) {
			ci.cancel();
		}
	}

	@Unique
	private boolean setKeyPressedCancelled = false;

	@Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"))
	private void redirect_setKeyPressed(Key key, boolean pressed, long window, int keycode, int scancode, int action, int modifiers) {
		setKeyPressedCancelled = KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.VANILLA_BIT, window, key, pressed ? (action != GLFW.GLFW_RELEASE ? action : GLFW.GLFW_PRESS) : GLFW.GLFW_RELEASE,
			modifiers, true);
	}

	@Inject(method = "onKey", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"), cancellable = true)
	private void injection_VANILLA_BIT(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (setKeyPressedCancelled) {
			setKeyPressedCancelled = false;
			ci.cancel();
		}
	}

	@Redirect(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V"))
	private void redirect_onKeyPressed(Key key, long window, int keycode, int scancode, int action, int modifiers) {
		// do nothing
	}

}
