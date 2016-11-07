package org.perfectable.repositable;

import org.perfectable.repositable.filter.ConjunctionFilter;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Filter {
	boolean matchesModule(String groupId, String artifactId);
	boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier);
	boolean matchesPackage(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging);
	boolean matchesSnapshot(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId);

	default Filter and(Filter other) {
		return ConjunctionFilter.of(this, other);
	}
}
