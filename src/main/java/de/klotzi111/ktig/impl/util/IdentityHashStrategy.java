package de.klotzi111.ktig.impl.util;

import it.unimi.dsi.fastutil.Hash.Strategy;
import net.minecraft.client.option.KeyBinding;

public class IdentityHashStrategy implements Strategy<KeyBinding> {

	public static final IdentityHashStrategy IDENTITY_HASH_STRATEGY = new IdentityHashStrategy();

	@Override
	public int hashCode(KeyBinding o) {
		return System.identityHashCode(o);
	}

	@Override
	public boolean equals(KeyBinding a, KeyBinding b) {
		return a == b;
	}

}
