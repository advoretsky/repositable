package org.perfectable.repositable;

import org.perfectable.repositable.filter.ConjunctionFilter;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Filter {
	boolean matchesModule(String groupId, String artifactId);
	boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier, Optional<String> classifier, String packaging);
	boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId);

	default Filter and(Filter other) {
		return ConjunctionFilter.of(this, other);
	}
}
