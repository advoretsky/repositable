package org.perfectable.repositable;

import org.perfectable.repositable.authorization.Authentication;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.UnauthenticatedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.authorization.AuthenticationException;
import org.perfectable.webable.handler.authorization.Authenticator;
import org.perfectable.webable.handler.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class StoringRequestAuthenticator implements Authenticator {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoringRequestAuthenticator.class);

	private final Group allowedUsers;

	private StoringRequestAuthenticator(Group allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public static StoringRequestAuthenticator allowing(Group allowedUsers) {
		return new StoringRequestAuthenticator(allowedUsers);
	}

	@Override
	public void visitNoAuthentication(HttpRequest request) {
		LOGGER.debug("Unauthenticated user was detected");
		request.select(Authentication.ATTRIBUTE).put(Authentication.EMPTY);
	}

	@Override
	public void visitAuthentication(HttpRequest request, String username, String password) throws AuthenticationException {
		User current;
		try {
			current = allowedUsers.authenticate(username, password);
		}
		catch (UnauthenticatedUserException e) {
			LOGGER.info("User {} was rejected while logging in", username);
			throw AuthenticationException.unauthorized();
		}
		LOGGER.debug("User {} was logged in", username);
		Authentication authentication = Authentication.of(current);
		request.select(Authentication.ATTRIBUTE).put(authentication);
	}
}
