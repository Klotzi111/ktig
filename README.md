# KTIG (short for: Keybinding Testing In GUI)

Works for Minecraft versions: 1.17+

This fabric library mod provides methods for testing whether Minecraft keybindings are pressed while the game shows a GUI. It also works when no GUI is shown.

The reason for making this library was because I have seen many mods having the problem of checking keys bound to keybindings being pressed in GUIs but do not deal with special cases like:
 - Mouse buttons
 - attributes and states of other mods

As of version 1.1.0 this library has support for:
 - [amecs-api](https://github.com/Siphalor/amecs-api "amecs-api on GitHub") with version 1.3.5 or higher
 - [nmuk](https://github.com/Siphalor/nmuk "nmuk on GitHub") with version 1.1.0 or higher

## Contributing
If you know a mod which adds such named features that causes problems with other mods, let me know by opening a issue.
The goal of this project is to deal with this keybinding problem in a central place.

## Usage
You can get it via jitpack or compile it yourself.

### Jitpack
Add the following in your build.gradle:

```groovy
repositories {
	maven {
		url "https://jitpack.io"
	}
}

dependencies {
	include(modApi("com.github.Klotzi111:ktig:main-SNAPSHOT"))
}
```

### Compile self
Do the following to use this library mod:
 - Download, build and publish this mod to your local maven repository (use the gradle task `publishToMavenLocal` for that)
 - Add the following in your build.gradle:
 
```groovy
repositories {
	mavenLocal()
}

dependencies {
	include(modApi("de.klotzi111:KTIG:1+"))
}
```

## Examples
(All the names from Minecraft code below are from the [yarn mappings](https://github.com/FabricMC/yarn "yarn on GitHub"))

### Basic
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

### Advanced
KTIG additionally has a more advanced and more powerful API. With this API you can get a event whenever a certain keybinding is about to be pressed or released and ignore it, allow it or even cancel the key event so that no other keybindings with the same key are triggered.

First you need to create a custom keybinding class that extends KeyBinding (as some point) and implements the API interface class `KeyBindingTriggerEventListener`:

```Java
public class KTIGTestKeyBinding extends KeyBinding implements KeyBindingTriggerEventListener {

	public KTIGTestKeyBinding(String translationKey, Type type, int code, String category) {
		super(translationKey, type, code, category);
	}

	// read the javadoc for more information
	@Override
	public boolean onTrigger(int triggerPoint, int action, Key key, boolean keyConsumed) {
		// do whatever you like in this method

		// you can also do the default behavior with
		boolean defaultRet = KeyBindingTriggerEventListener.onTriggerDefaultKeyBinding(this, triggerPoint, action, key, keyConsumed);

		// return whether to cancel the key event. See javadoc
		return defaultRet;
	}

}
```

Then you can register a instance of your custom keybinding class as you would with a normal KeyBinding:

```Java
KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KTIGTestKeyBinding("key.MOD.NAME", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.MOD"));

```