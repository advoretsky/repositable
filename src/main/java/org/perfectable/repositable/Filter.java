package org.perfectable.repositable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Filter {
	boolean matchesModule(String groupId, String artifactId);
	boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging);
	boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId);
}
