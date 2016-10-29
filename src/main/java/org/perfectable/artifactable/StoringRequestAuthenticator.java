package org.perfectable.artifactable;

import org.perfectable.artifactable.authorization.Authentication;
import org.perfectable.artifactable.authorization.Group;
import org.perfectable.artifactable.authorization.UnauthenticatedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.webable.handler.authorization.AuthenticationException;
import org.perfectable.webable.handler.authorization.Authenticator;
import org.perfectable.webable.handler.HttpRequest;

final class StoringRequestAuthenticator implements Authenticator {

	private final Group allowedUsers;

	private StoringRequestAuthenticator(Group allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public static StoringRequestAuthenticator allowing(Group allowedUsers) {
		return new StoringRequestAuthenticator(allowedUsers);
	}

	@Override
	public void visitNoAuthentication(HttpRequest request) {
		request.select(Authentication.ATTRIBUTE).put(Authentication.EMPTY);
	}

	@Override
	public void visitAuthentication(HttpRequest request, String username, String password) throws AuthenticationException {
		User current;
		try {
			current = allowedUsers.authenticate(username, password);
		}
		catch (UnauthenticatedUserException e) {
			throw AuthenticationException.unauthorized();
		}
		Authentication authentication = Authentication.of(current);
		request.select(Authentication.ATTRIBUTE).put(authentication);
	}
}
