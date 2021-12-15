package de.klotzi111.ktig.impl.util;

import java.util.Objects;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class MinimumModVersionChecker implements ModSemanticVersionChecker {
	public final SemanticVersion minimumRequiredVersion;

	public MinimumModVersionChecker(SemanticVersion minimumRequiredVersion) {
		this.minimumRequiredVersion = minimumRequiredVersion;
	}

	public MinimumModVersionChecker(String minimumRequiredVersion) {
		try {
			this.minimumRequiredVersion = SemanticVersion.parse(minimumRequiredVersion);
		} catch (VersionParsingException e) {
			throw new IllegalStateException("Could not parse version string", e);
		}
	}

	@SuppressWarnings("deprecation") // can not use the new/non-deprecated method because it does not exist on older versions of fabric
	@Override
	public boolean isSemanticVersionSupported(SemanticVersion modVersion) {
		return modVersion.compareTo(minimumRequiredVersion) >= 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(minimumRequiredVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MinimumModVersionChecker other = (MinimumModVersionChecker) obj;
		return Objects.equals(minimumRequiredVersion.getFriendlyString(), other.minimumRequiredVersion.getFriendlyString());
	}

}
