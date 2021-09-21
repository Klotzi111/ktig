package de.klotzi111.ktig.api;

public class KeyBindingTriggerPoints {

	/**
	 * With this trigger point the keybinding will be triggered directly when the key callback arrives.
	 * Thats even before the window is checked. So if some mod adds a extra window for the game and that window receives key or mouse pressed the keybinding will be triggered non the less.
	 */
	public static int ALL_WINDOWS_BIT = 0b00001;

	/**
	 * With this trigger point the keybinding will be triggered directly after the window is checked to be the minecraft main window.
	 */
	public static int MAIN_WINDOW_BIT = 0b00010;

	/**
	 * With this trigger point the keybinding will be triggered only if the currently active screen did NOT return 'responded' to the keybinding.
	 */
	public static int NON_SCREEN_PROCESSED_BIT = 0b00100;

	/**
	 * With this trigger point the keybinding will be triggered only if there is no screen currently open (or if the screen has set passEvents to true).
	 * <br>
	 * This is very close to the vanilla behavior. It is triggered short before the vanilla logic. This is useful if you want to cancel the event for the vanilla logic.
	 */
	public static int NO_SCREEN_BIT = 0b01000;

	/**
	 * With this trigger point the keybinding will NOT be triggered if the vanilla logic triggers it.
	 * <br>
	 * This disables the default vanilla behavior.
	 */
	public static int NO_VANILLA_BIT = 0b10000;

	public static boolean areAllBitsSet(int bits, int checkBits) {
		return (bits & checkBits) == checkBits;
	}

}
