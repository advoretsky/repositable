package org.perfectable.artifactable.authorization;

public final class User {
	private final String username;
	private final String password;

	private User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public static User create(String username, String password) {
		return new User(username, password);
	}

	public void authenticate(String username, String password) throws UnauthenticatedUserException {
		if (!username.equals(this.username) || !password.equals(this.password)) {
			throw new UnauthenticatedUserException();
		}
	}
}
