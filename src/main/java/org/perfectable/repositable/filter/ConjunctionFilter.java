package org.perfectable.repositable.filter;

import com.google.common.collect.ImmutableSet;
import org.perfectable.repositable.Filter;
import org.perfectable.repositable.ModuleIdentifier;
import org.perfectable.repositable.PackageIdentifier;
import org.perfectable.repositable.VersionIdentifier;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public final class ConjunctionFilter implements Filter {
	private final ImmutableSet<Filter> components;

	public static ConjunctionFilter of(Filter... components) {
		return new ConjunctionFilter(ImmutableSet.copyOf(components));
	}

	public static ConjunctionFilter of(Set<Filter> components) {
		return new ConjunctionFilter(ImmutableSet.copyOf(components));
	}

	private ConjunctionFilter(ImmutableSet<Filter> components) {
		this.components = components;
	}

	@Override
	public boolean matchesModule(String groupId, String artifactId) {
		for (Filter component : components) {
			if (!component.matchesModule(groupId, artifactId)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare,
								  Optional<String> versionQualifier) {
		for (Filter component : components) {
			if (!component.matchesVersion(moduleIdentifier, versionBare, versionQualifier)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean matchesPackage(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		for (Filter component : components) {
			if (!component.matchesPackage(versionIdentifier, classifier, packaging)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean matchesSnapshot(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId) {
		for (Filter component : components) {
			if (!component.matchesSnapshot(packageIdentifier, timestamp, buildId)) {
				return false;
			}
		}
		return true;
	}

}
