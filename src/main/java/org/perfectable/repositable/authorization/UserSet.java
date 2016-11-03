package org.perfectable.repositable.authorization;

@FunctionalInterface
public interface UserSet {
	boolean contains(User candidate);

	default UserSet intersection(UserSet other) {
		return IntersectionUserSet.of(this, other);
	}
}
