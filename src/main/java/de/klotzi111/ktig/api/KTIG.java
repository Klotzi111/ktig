package de.klotzi111.ktig.api;

import org.jetbrains.annotations.ApiStatus;

import de.klotzi111.ktig.impl.KTIGHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;

/**
 * Base class of this library. It contains the most important features of this library
 */
@Environment(EnvType.CLIENT)
public class KTIG {

	/**
	 * FOR INTERNAL USE ONLY
	 */
	@ApiStatus.Internal
	public static final Int2ObjectOpenHashMap<ObjectOpenCustomHashSet<KeyBinding>> TRIGGERPOINT_KEYBINDINGS = new Int2ObjectOpenHashMap<>();

	/**
	 * FOR INTERNAL USE ONLY
	 */
	@ApiStatus.Internal
	public static boolean CURRENT_EVENT_KEY_CONSUMED = false;

	/**
	 * Registers the keyBinding for all the trigger points in {@code triggerPoints}
	 *
	 * @param keyBinding
	 * @param triggerPoints
	 * @return a bit mask containing all the bits of the trigger points for which the keyBinding was registered
	 */
	public static int registerKeyBindingForTriggerPoints(KeyBinding keyBinding, int triggerPoints) {
		return KTIGHelper.doAndSumForAllBits(triggerPoints, bit -> addKeyBinding(bit, keyBinding));
	}

	/**
	 * Unregisters the keyBinding for all the trigger points in {@code triggerPoints}
	 *
	 * @param keyBinding
	 * @param triggerPoints
	 * @return a bit mask containing all the bits of the trigger points for which the keyBinding was unregistered
	 */
	public static int unregisterKeyBindingForTriggerPoints(KeyBinding keyBinding, int triggerPoints) {
		return KTIGHelper.doAndSumForAllBits(triggerPoints, bit -> removeKeyBinding(bit, keyBinding));
	}

	/**
	 * Gets all the trigger points for which the keyBinding is registered
	 *
	 * @param keyBinding
	 * @param triggerPoints
	 * @return a bit mask containing all the bits of the trigger points for which the keyBinding is registered
	 */
	public static int getKeyBindingRegisteredForTriggerPoints(KeyBinding keyBinding) {
		return KTIGHelper.doAndSumForAllBits(-1, bit -> containsKeyBinding(bit, keyBinding));
	}

	/**
	 *
	 * @param triggerPoint
	 * @param keyBinding
	 * @return whether the keyBinding was added (not already contained)
	 */
	private static boolean addKeyBinding(int triggerPoint, KeyBinding keyBinding) {
		ObjectOpenCustomHashSet<KeyBinding> set = TRIGGERPOINT_KEYBINDINGS.get(triggerPoint);
		if (set == null) {
			set = KTIGHelper.createKeyBindingHashSet();
			TRIGGERPOINT_KEYBINDINGS.put(triggerPoint, set);
		}
		return set.add(keyBinding);
	}

	/**
	 *
	 * @param triggerPoint
	 * @param keyBinding
	 * @return whether the keyBinding was removed (was contained)
	 */
	private static boolean removeKeyBinding(int triggerPoint, KeyBinding keyBinding) {
		ObjectOpenCustomHashSet<KeyBinding> set = TRIGGERPOINT_KEYBINDINGS.get(triggerPoint);
		if (set == null) {
			return false;
		}
		return set.remove(keyBinding);
	}

	/**
	 *
	 * @param triggerPoint
	 * @param keyBinding
	 * @return whether the keyBinding is contained
	 */
	public static boolean containsKeyBinding(int triggerPoint, KeyBinding keyBinding) {
		ObjectOpenCustomHashSet<KeyBinding> set = TRIGGERPOINT_KEYBINDINGS.get(triggerPoint);
		if (set == null) {
			return false;
		}
		return set.contains(keyBinding);
	}

	/**
	 * This method tests whether the given keyBinding's press count since the last check is odd.
	 * <br>
	 * The presses will be consumed.
	 * <br>
	 * Examples: Press count -> return value
	 * <br>
	 * 0 -> false
	 * <br>
	 * 1 -> true
	 * <br>
	 * 2 -> false
	 * <br>
	 * 3 -> true
	 *
	 * @param keyBinding
	 *
	 * @return whether the keybinding's press count is odd
	 */
	public static boolean wasKeyBindingPressed(KeyBinding keyBinding) {
		return getPressedCount(keyBinding) % 2 == 1;
	}

	/**
	 * This method returns the press count of the given keyBinding since the last check.
	 * <br>
	 * The presses will be consumed.
	 *
	 * @param keyBinding
	 *
	 * @return the keybinding's press count
	 */
	public static int getPressedCount(KeyBinding keyBinding) {
		int count = 0;
		while (keyBinding.wasPressed()) {
			count++;
		}
		return count;
	}

}
