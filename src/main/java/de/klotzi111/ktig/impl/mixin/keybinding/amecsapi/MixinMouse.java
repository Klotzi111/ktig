package de.klotzi111.ktig.impl.mixin.keybinding.amecsapi;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPress;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPressDefault;
import de.siphalor.amecs.api.KeyBindingUtils;
import de.siphalor.amecs.impl.duck.IMouse;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;

// priority to be before amecs api
@Environment(EnvType.CLIENT)
@Mixin(value = Mouse.class, priority = -2200)
public class MixinMouse {

	@Final
	@Shadow
	private MinecraftClient client;

	@Unique
	private Int2DoubleOpenHashMap TRIGGERPOINT_EVENTDELTAWHEEL = new Int2DoubleOpenHashMap();

	@Unique
	private int scrollPressedCancelled = 0;

	private boolean onScrollReceived(int triggerPoint, double deltaY, ProcessKeyBindingTriggerKeyAndPress triggerFunction, boolean manualDeltaWheel, float g) {
		int scrollCount = 0;
		if (manualDeltaWheel) {
			// from minecraft but patched
			// this code might be wrong when the vanilla mc code changes
			double eventDeltaWheel = TRIGGERPOINT_EVENTDELTAWHEEL.getOrDefault(triggerPoint, 0);
			if (eventDeltaWheel != 0.0D && Math.signum(deltaY) != Math.signum(eventDeltaWheel)) {
				eventDeltaWheel = 0.0D;
			}

			eventDeltaWheel += deltaY;
			scrollCount = (int) eventDeltaWheel;
			if (scrollCount == 0) {
				TRIGGERPOINT_EVENTDELTAWHEEL.put(triggerPoint, eventDeltaWheel);
				return false;
			}

			eventDeltaWheel -= scrollCount;
			TRIGGERPOINT_EVENTDELTAWHEEL.put(triggerPoint, eventDeltaWheel);
			// -from minecraft
		} else {
			scrollCount = (int) g;
		}

		InputUtil.Key keyCode = KeyBindingUtils.getKeyFromScroll(scrollCount);

		scrollCount = Math.abs(scrollCount);

		int scrollCountCopy = scrollCount;
		int cancelledPresses = 0;
		while (scrollCount > 0) {
			cancelledPresses += triggerFunction.processKeyBindingTrigger(keyCode, true) ? 1 : 0;
			triggerFunction.processKeyBindingTrigger(keyCode, false);
			scrollCount--;
		}

		scrollPressedCancelled = cancelledPresses;
		return cancelledPresses == scrollCountCopy;
	}

	@Unique
	private double decValAndReset(double d) {
		double ret = d - scrollPressedCancelled;
		scrollPressedCancelled = 0;
		return ret;
	}

	// could also inject at head
	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", ordinal = 0), cancellable = true)
	private void injection_ALL_WINDOWS(long window, double horizontal, double vertical, CallbackInfo ci) {
		// this is the line calculating the deltaY from Minecraft Mouse
		// this line must be updated if it changes in the MC code
		double deltaY = (client.options.discreteMouseScroll ? Math.signum(vertical) : vertical) * client.options.mouseWheelSensitivity;

		int triggerPoint = KeyBindingTriggerPoints.ALL_WINDOWS_BIT;
		if (onScrollReceived(triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	private double firstSetDeltaY_after_ALL_WINDOWS(double d) {
		return decValAndReset(d);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void injection_MAIN_WINDOW(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		int triggerPoint = KeyBindingTriggerPoints.MAIN_WINDOW_BIT;
		if (onScrollReceived(triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0), ordinal = 2)
	private double after_MAIN_WINDOW(double d) {
		return decValAndReset(d);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void injection_NON_SCREEN_PROCESSED(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		if (((IMouse) this).amecs$getMouseScrolledEventUsed()) {
			return;
		}
		int triggerPoint = KeyBindingTriggerPoints.NON_SCREEN_PROCESSED_BIT;
		if (onScrollReceived(triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			// do not cancel. It does not actually cancel anything relevant.
			// But we would cancel the fabric api afterMouseScrollEvent and we do not want that
			// ci.cancel();
		}
	}

	// does not actually affect anything because the method ends after this. But if it is here for compatibility with other mods
	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", ordinal = 0), ordinal = 2)
	private double after_NON_SCREEN_PROCESSED(double d) {
		return decValAndReset(d);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void injection_NO_SCREEN(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		// we are here in the else branch of "this.client.currentScreen != null" meaning currentScreen == null
		int triggerPoint = KeyBindingTriggerPoints.NO_SCREEN_BIT;
		if (onScrollReceived(triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;", ordinal = 0), ordinal = 2)
	private double after_NO_SCREEN(double d) {
		return decValAndReset(d);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void injection_isSpectator_VANILLA_BIT(long window, double rawX, double rawY, CallbackInfo ci, double deltaY, float g) {
		// we are here in the else branch of "this.client.currentScreen != null" meaning currentScreen == null
		int triggerPoint = KeyBindingTriggerPoints.VANILLA_BIT;
		if (onScrollReceived(triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), false, g)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0), ordinal = 2)
	private double after_VANILLA_BIT(double d) {
		return decValAndReset(d);
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0), ordinal = 0)
	private float g_after_VANILLA_BIT(float g) {
		return (float) decValAndReset(g);
	}

}
