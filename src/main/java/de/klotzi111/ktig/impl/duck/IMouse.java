package de.klotzi111.ktig.impl.duck;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IMouse {
	Int2DoubleOpenHashMap ktig$getTRIGGERPOINT_EVENTDELTAWHEEL();

	void ktig$setScrollPressedCancelled(int scrollPressedCancelled);

	double ktig$decrementValueAndResetScrollPressedCancelled(double d);
}
