# KTIG (short for: Keybinding Testing In GUI)

This fabric library mod provides methods for testing whether Minecraft keybindings are pressed while the game shows a GUI. It also works when no GUI is shown.

The reason for making this library was because I have seen many mods having the problem of checking keys bound to keybindings being pressed in GUIs but do not deal with special cases like:
 - Mouse buttons
 - attributes and states of other mods

As of version 1.0.0 this library has support for:
 - [amecs-api](https://github.com/Siphalor/amecs-api "amecs-api on GitHub") with version 1.2.1 or higher
 - [nmuk](https://github.com/Siphalor/nmuk "nmuk on GitHub") with version 1.1.0 or higher

## Contributing
If you know a mod which adds such named features that causes problems with other mods, let me know by opening a issue.
The goal of this project is to deal with this keybinding problem in a central place.

## Usage
As of now there is no maven repository to download it from.
Do the following to use this library mod:
 - Download, build and publish this mod to your local maven repository
 - Add the following in your build.gradle:
 
```groovy
repositories {
    mavenLocal()
}

dependencies {
    modImplementation "de.klotzi111:KTIG:1+"
    include "de.klotzi111:KTIG:1+"
}
```

## Example
(All the names from Minecraft code below are from the [yarn mappings](https://github.com/FabricMC/yarn "yarn on GitHub"))

To test a keybinding in gui first register the keybinding to be triggered when in gui:

```Java
// replace the next line with you KeyBinding creation and registration
KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.MOD.NAME", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.MOD"));
// then register it for some trigger points
// here MAIN_WINDOW_BIT is used. This is the trigger point usually used when you want to receive all key events in all guis
// NO_VANILLA_BIT is used to avoid being triggered twice (on MAIN_WINDOW_BIT and on vanilla trigger) when there is no gui currently shown
KTIG.registerKeyBindingForTriggerPoints(keyBinding, KeyBindingTriggerPoints.MAIN_WINDOW_BIT | KeyBindingTriggerPoints.NO_VANILLA_BIT);
```

Then to test whether the keybinding was pressed use the normal way of checking it in a loop:

```Java
while (keyBinding.wasPressed()) {
	// do something when it was pressed
}
```