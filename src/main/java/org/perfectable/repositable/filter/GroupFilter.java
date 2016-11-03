package org.perfectable.repositable.filter;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.ModuleIdentifier;
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
	public boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId) {
		return versionIdentifier.hasGroupId(groupId);
	}

	@Override
	public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		return moduleIdentifier.hasGroupId(groupId);
	}
}
