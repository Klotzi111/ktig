package de.klotzi111.ktig.impl.version;

import java.util.Optional;

import net.fabricmc.loader.api.*;

public class MinecraftVersionHelper {

	public static Version MINECRAFT_VERSION = null;
	public static SemanticVersion SEMANTIC_MINECRAFT_VERSION = null;

	static {
		getMinecraftVersion();
	}

	public static SemanticVersion parseSemanticVersion(String version) {
		try {
			return SemanticVersion.parse(version);
		} catch (VersionParsingException e) {
			// this should really never happen, because we carefully craft the version strings statically (at compile time)
			throw new IllegalStateException("Could not parse semantic version for minecraft version: " + version, e);
		}
	}

	private static void getMinecraftVersion() {
		Optional<ModContainer> minecraftModContainer = FabricLoader.getInstance().getModContainer("minecraft");
		if (!minecraftModContainer.isPresent()) {
			throw new IllegalStateException("Minecraft not available?!?");
		}
		MINECRAFT_VERSION = minecraftModContainer.get().getMetadata().getVersion();
		if (MINECRAFT_VERSION instanceof SemanticVersion) {
			SEMANTIC_MINECRAFT_VERSION = (SemanticVersion) MINECRAFT_VERSION;
		} else {
			throw new IllegalStateException("Minecraft version is no SemVer!");
		}
	}

}
