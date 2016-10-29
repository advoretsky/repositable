package org.perfectable.repositable.authorization;

import org.perfectable.webable.handler.HttpRequestAttribute;

public interface Authentication {
	HttpRequestAttribute<Authentication> ATTRIBUTE = HttpRequestAttribute.named("authorization");

	Authentication EMPTY = () -> { throw new UnauthenticatedUserException(); };

	User requireUser() throws UnauthenticatedUserException;

	static Authentication of(User current) {
		return () -> current;
	}
}
