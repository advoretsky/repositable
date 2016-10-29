package org.perfectable.artifactable;

import java.time.LocalDateTime;
import java.util.Optional;

public class StabilityFilter implements Filter {

	public static final StabilityFilter RELEASE = new StabilityFilter(false);
	public static final StabilityFilter SNAPSHOT = new StabilityFilter(true);

	private final boolean allowSnapshots;

	private StabilityFilter(boolean allowSnapshots) {
		this.allowSnapshots = allowSnapshots;
	}

	@Override
	public boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId) {
		return allowSnapshots;
	}

	@Override
	public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		boolean isSnapshot = versionModifier.isPresent() && versionModifier.get().equals("SNAPSHOT");
		return allowSnapshots == isSnapshot;
	}
}
