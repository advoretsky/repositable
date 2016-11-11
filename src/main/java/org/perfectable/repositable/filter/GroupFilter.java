package org.perfectable.repositable.filter;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.ModuleIdentifier;
import org.perfectable.repositable.PackageIdentifier;
import org.perfectable.repositable.VersionIdentifier;

import java.time.LocalDateTime;
import java.util.Optional;

public final class GroupFilter implements Filter {
	private final String acceptedGroupId;

	public static GroupFilter of(String acceptedGroupId) {
		return new GroupFilter(acceptedGroupId);
	}

	private GroupFilter(String acceptedGroupId) {
		this.acceptedGroupId = acceptedGroupId;
	}

	@Override
	public boolean matchesModule(String groupId, String artifactId) {
		return groupId.equals(this.acceptedGroupId);
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
