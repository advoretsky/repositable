package org.perfectable.repositable.authorization;

@FunctionalInterface
public interface UserSet {
	boolean contains(User candidate);
}
