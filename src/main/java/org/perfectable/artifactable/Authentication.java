package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpRequestAttribute;

public interface Authentication {
	HttpRequestAttribute<Authentication> ATTRIBUTE = HttpRequestAttribute.named("authentication");

	Authentication EMPTY = () -> { throw new UnauthenticatedUserException(); };

	User requireUser() throws UnauthenticatedUserException;

	static Authentication of(User current) {
		return () -> current;
	}
}
