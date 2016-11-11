package org.perfectable.repositable.filter;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.ModuleIdentifier;
import org.perfectable.repositable.PackageIdentifier;
import org.perfectable.repositable.VersionIdentifier;

import java.time.LocalDateTime;
import java.util.Optional;

public final class GroupFilter implements Filter {
	private final String groupId;

	public static GroupFilter of(String groupId) {
		return new GroupFilter(groupId);
	}

	private GroupFilter(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public boolean matchesModule(String groupId, String artifactId) {
		return groupId.equals(this.groupId);
	}

	@Override
	public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare,
								  Optional<String> versionQualifier) {
		return moduleIdentifier.matches(this);
	}

	@Override
	public boolean matchesPackage(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		return versionIdentifier.matches(this);
	}

	@Override
	public boolean matchesSnapshot(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId) {
		return packageIdentifier.matches(this);
	}
}
