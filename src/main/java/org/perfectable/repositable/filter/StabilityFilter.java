package org.perfectable.repositable.filter;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.ModuleIdentifier;
import org.perfectable.repositable.PackageIdentifier;
import org.perfectable.repositable.VersionIdentifier;

import java.time.LocalDateTime;
import java.util.Optional;

public final class StabilityFilter implements Filter {

	public static final StabilityFilter RELEASE = new StabilityFilter(false);
	public static final StabilityFilter SNAPSHOT = new StabilityFilter(true);

	private final boolean allowSnapshots;

	private StabilityFilter(boolean allowSnapshots) {
		this.allowSnapshots = allowSnapshots;
	}

	@Override
	public boolean matchesModule(String groupId, String artifactId) {
		return !allowSnapshots;
	}

	@Override
	public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier) {
		boolean isSnapshot = versionQualifier.isPresent() && versionQualifier.get().equals("SNAPSHOT");
		return allowSnapshots == isSnapshot;
	}

	@Override
	public boolean matchesPackage(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		return versionIdentifier.matches(this);
	}

	@Override
	public boolean matchesSnapshot(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId) {
		return allowSnapshots;
	}
}
