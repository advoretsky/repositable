package org.perfectable.repositable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Filter {
	boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId);
	boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging);
}
