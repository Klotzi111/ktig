package de.klotzi111.ktig.impl.mixinhelper;

import net.minecraft.client.util.InputUtil.Key;

@FunctionalInterface
public interface ProcessKeyBindingTriggerKeyAndPress {
	public boolean processKeyBindingTrigger(Key key, boolean pressed);
}
