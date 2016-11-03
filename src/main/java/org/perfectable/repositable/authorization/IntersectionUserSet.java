package org.perfectable.repositable.authorization;

import com.google.common.collect.ImmutableList;

final class IntersectionUserSet implements UserSet {
	private final ImmutableList<UserSet> components;

	private IntersectionUserSet(ImmutableList<UserSet> components) {
		this.components = components;
	}

	public static UserSet of(UserSet... components) {
		return new IntersectionUserSet(ImmutableList.copyOf(components));
	}

	@Override
	public boolean contains(User candidate) {
		for (UserSet component : components) {
			if (!component.contains(candidate)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public UserSet intersection(UserSet other) {
		ImmutableList<UserSet> newComponents = ImmutableList.<UserSet>builder().addAll(components).add(other).build();
		return new IntersectionUserSet(newComponents);
	}
}
