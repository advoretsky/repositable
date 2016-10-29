package org.perfectable.repositable.authorization;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class Group {
	private final ImmutableSet<User> userSet;

	private Group(ImmutableSet<User> userSet) {
		this.userSet = userSet;
	}

	public static Group create() {
		return new Group(ImmutableSet.of());
	}

	public static Group of(Set<User> userSet) {
		return new Group(ImmutableSet.copyOf(userSet));
	}

	public Group join(User newUser) {
		ImmutableSet<User> newUsers = ImmutableSet.<User>builder().addAll(userSet).add(newUser).build();
		return new Group(newUsers);
	}

	public boolean contains(User candidate) {
		return userSet.contains(candidate);
	}

	public User authenticate(String username, String password) throws UnauthenticatedUserException {
		for(User candidate : userSet) {
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
