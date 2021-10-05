package de.klotzi111.ktig.impl.mixin.keybinding.vanilla;

import org.lwjgl.glfw.GLFW;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
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
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil.Key;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public class MixinMouse {

	// could also inject at head
	@Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", ordinal = 0), cancellable = true)
	private void injection_ALL_WINDOWS(long window, int button, int action, int mods, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromMouse(button);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.ALL_WINDOWS_BIT, window, keyObject, action, 0, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseButton", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/MinecraftClient;IS_SYSTEM_MAC:Z", ordinal = 0), cancellable = true)
	private void injection_MAIN_WINDOW(long window, int button, int action, int mods, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromMouse(button);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.MAIN_WINDOW_BIT, window, keyObject, action, 0, true)) {
			ci.cancel();
		}
	}

	// after array access and check to false
	@Inject(method = "onMouseButton", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 2), cancellable = true)
	private void injection_NON_SCREEN_PROCESSED(long window, int button, int action, int mods, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromMouse(button);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.NON_SCREEN_PROCESSED_BIT, window, keyObject, action, 0, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "onMouseButton", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 1), cancellable = true)
	private void injection_NO_SCREEN(long window, int button, int action, int mods, CallbackInfo ci) {
		Key keyObject = KeyBindingManagerLoader.INSTANCE.getKeyFromMouse(button);
		if (KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.NO_SCREEN_BIT, window, keyObject, action, 0, true)) {
			ci.cancel();
		}
	}

	@Redirect(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"))
	private void redirect_setKeyPressed(Key key, boolean pressed, long window, int button, int action, int mods) {
		// not cancellable
		KTIGHelper.processKeyBindingTrigger(KeyBindingTriggerPoints.VANILLA_BIT, window, key, pressed ? GLFW.GLFW_PRESS : GLFW.GLFW_RELEASE, 0, false);
	}

	@Redirect(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;onKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;)V"))
	private void redirect_onKeyPressed(Key key, long window, int button, int action, int mods) {
		// do nothing
	}

}
