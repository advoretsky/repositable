package org.perfectable.repositable.authorization;

public final class User {
	private static final String REPRESENTATION_FORMAT = "User(%s)";

	private final String username;
	private final String password;

	private User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public static User create(String username, String password) {
		return new User(username, password);
	}

	public void authenticate(String providedUsername, String providedPassword) throws UnauthenticatedUserException {
		if (!providedUsername.equals(this.username) || !providedPassword.equals(this.password)) {
			throw new UnauthenticatedUserException();
		}
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, username);
	}
}
