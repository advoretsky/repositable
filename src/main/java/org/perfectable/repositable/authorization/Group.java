package org.perfectable.repositable.authorization;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class Group implements UserSet {
	private final ImmutableSet<User> users;

	private Group(ImmutableSet<User> users) {
		this.users = users;
	}

	public static Group create() {
		return new Group(ImmutableSet.of());
	}

	public static Group of(Set<User> users) {
		return new Group(ImmutableSet.copyOf(users));
	}

	public Group join(User newUser) {
		ImmutableSet<User> newUsers = ImmutableSet.<User>builder().addAll(users).add(newUser).build();
		return new Group(newUsers);
	}

	@Override
	public boolean contains(User candidate) {
		return users.contains(candidate);
	}

	public User authenticate(String username, String password) throws UnauthenticatedUserException {
		for(User candidate : users) {
			try {
				candidate.authenticate(username, password);
				return candidate;
			}
			catch(UnauthenticatedUserException e) { // NOPMD
				// try next
			}
		}
		throw new UnauthenticatedUserException();
	}
}
