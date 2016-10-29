package org.perfectable.repositable;

import com.google.common.collect.ImmutableSet;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

public abstract class CompositeFilter implements Filter {
	protected final ImmutableSet<Filter> components;

	protected CompositeFilter(ImmutableSet<Filter> components) {
		this.components = components;
	}

	public static CompositeFilter conjunction(Set<Filter> components) {
		return new ConjunctiveFilter(ImmutableSet.copyOf(components));
	}

	private static class ConjunctiveFilter extends CompositeFilter {
		public ConjunctiveFilter(ImmutableSet<Filter> filterSet) {
			super(filterSet);
		}

		@Override
		public boolean matchesSnapshot(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId) {
			for (Filter component : components) {
				if(!component.matchesSnapshot(versionIdentifier, timestamp, buildId)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean matchesVersion(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
			for (Filter component : components) {
				if(!component.matchesVersion(moduleIdentifier, versionBare, versionModifier, classifier, packaging)) {
					return false;
				}
			}
			return true;
		}
	}
}
