package de.klotzi111.ktig.impl.mixin.keybinding.amecsapi;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPressDefault;
import de.klotzi111.ktig.impl.mixinimpl.MixinMouseImpl;
import de.siphalor.amecs.impl.duck.IMouse;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

// priority to be before amecs api
@Mixin(value = Mouse.class, priority = -2200)
public class MixinMouse implements de.klotzi111.ktig.impl.duck.IMouse {

	@Final
	@Shadow
	private MinecraftClient client;

	@Unique
	private Int2DoubleOpenHashMap TRIGGERPOINT_EVENTDELTAWHEEL = new Int2DoubleOpenHashMap();

	@Override
	public Int2DoubleOpenHashMap ktig$getTRIGGERPOINT_EVENTDELTAWHEEL() {
		return TRIGGERPOINT_EVENTDELTAWHEEL;
	}

	@Unique
	private int scrollPressedCancelled = 0;

	@Override
	public void ktig$setScrollPressedCancelled(int scrollPressedCancelled) {
		this.scrollPressedCancelled = scrollPressedCancelled;
	}

	@Override
	public double ktig$decrementValueAndResetScrollPressedCancelled(double d) {
		double ret = d - scrollPressedCancelled;
		scrollPressedCancelled = 0;
		return ret;
	}

	// could also inject at head
	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHandle()J", ordinal = 0), cancellable = true)
	private void injection_ALL_WINDOWS(long window, double horizontal, double vertical, CallbackInfo ci) {
		double deltaY = MixinMouseImpl.calculateDeltaY(client, vertical);

		int triggerPoint = KeyBindingTriggerPoints.ALL_WINDOWS_BIT;
		if (MixinMouseImpl.onScrollReceived(this, triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
	private double firstSetDeltaY_after_ALL_WINDOWS(double d) {
		return ktig$decrementValueAndResetScrollPressedCancelled(d);
	}

	@Inject(
		method = "onMouseScroll",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0),
		locals = LocalCapture.CAPTURE_FAILSOFT,
		cancellable = true)
	private void injection_MAIN_WINDOW(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		int triggerPoint = KeyBindingTriggerPoints.MAIN_WINDOW_BIT;
		if (MixinMouseImpl.onScrollReceived(this, triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", ordinal = 0), ordinal = 2)
	private double after_MAIN_WINDOW(double d) {
		return ktig$decrementValueAndResetScrollPressedCancelled(d);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void injection_NON_SCREEN_PROCESSED(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		if (((IMouse) this).amecs$getMouseScrolledEventUsed()) {
			return;
		}
		int triggerPoint = KeyBindingTriggerPoints.NON_SCREEN_PROCESSED_BIT;
		if (MixinMouseImpl.onScrollReceived(this, triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			// do not cancel. It does not actually cancel anything relevant.
			// But we would cancel the fabric api afterMouseScrollEvent and we do not want that
			// ci.cancel();
		}
	}

	// does not actually affect anything because the method ends after this. But if it is here for compatibility with other mods
	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", ordinal = 0), ordinal = 2)
	private double after_NON_SCREEN_PROCESSED(double d) {
		return ktig$decrementValueAndResetScrollPressedCancelled(d);
	}

	@Inject(
		method = "onMouseScroll",
		at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;", ordinal = 0),
		locals = LocalCapture.CAPTURE_FAILSOFT,
		cancellable = true)
	private void injection_NO_SCREEN(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
		// we are here in the else branch of "this.client.currentScreen != null" meaning currentScreen == null
		int triggerPoint = KeyBindingTriggerPoints.NO_SCREEN_BIT;
		if (MixinMouseImpl.onScrollReceived(this, triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), true, 0)) {
			ci.cancel();
		}
	}

	//

	@ModifyVariable(
		method = "onMouseScroll",
		at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;", ordinal = 0),
		ordinal = 2)
	private double after_NO_SCREEN(double d) {
		return ktig$decrementValueAndResetScrollPressedCancelled(d);
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0), ordinal = 2)
	private double after_VANILLA_BIT(double d) {
		return ktig$decrementValueAndResetScrollPressedCancelled(d);
	}

}
