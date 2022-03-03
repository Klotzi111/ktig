package de.klotzi111.ktig.impl.mixin.keybinding.amecsapi.versioned;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.klotzi111.ktig.api.KeyBindingTriggerPoints;
import de.klotzi111.ktig.impl.duck.IMouse;
import de.klotzi111.ktig.impl.mixinhelper.ProcessKeyBindingTriggerKeyAndPressDefault;
import de.klotzi111.ktig.impl.mixinimpl.MixinMouseImpl;
import net.minecraft.client.Mouse;

// priority to be before amecs api
@Mixin(value = Mouse.class, priority = -2200)
public abstract class MixinMouse_1_14 implements IMouse {

	@Inject(
		method = "onMouseScroll",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void injection_isSpectator_VANILLA_BIT(long window, double rawX, double rawY, CallbackInfo ci, double deltaY, float g) {
		// we are here in the else branch of "this.client.currentScreen != null" meaning currentScreen == null
		int triggerPoint = KeyBindingTriggerPoints.VANILLA_BIT;
		if (MixinMouseImpl.onScrollReceived(this, triggerPoint, deltaY, new ProcessKeyBindingTriggerKeyAndPressDefault(window, triggerPoint), false, (int) g)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 0), ordinal = 0)
	private float g_after_VANILLA_BIT(float g) {
		return (float) ktig$decrementValueAndResetScrollPressedCancelled(g);
	}

}
