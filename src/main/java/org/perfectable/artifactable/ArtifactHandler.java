package org.perfectable.artifactable;

import org.perfectable.artifactable.authorization.Authentication;
import org.perfectable.artifactable.authorization.UnauthenticatedUserException;
import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public class ArtifactHandler implements RequestHandler {
	private final Repositories repositories;
	private final Locator locator;

	public static ArtifactHandler of(Repositories repositories, Locator locator) {
		return new ArtifactHandler(repositories, locator);
	}

	private ArtifactHandler(Repositories repositories, Locator locator) {
		this.repositories = repositories;
		this.locator = locator;
	}

	interface Locator {
		ArtifactLocation createLocation(String path);
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		ArtifactLocation location = locator.createLocation(path);
		switch(request.method()) {
			case GET:
				Optional<Artifact> artifact = location.find(repositories);
				if(!artifact.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return location.createResponse(artifact.get());
			case PUT:
				Authentication authentication = request.select(Authentication.ATTRIBUTE).get();
				User uploader;
				try {
					uploader = authentication.requireUser();
				}
				catch (UnauthenticatedUserException e) {
					return HttpResponse.status(HttpStatus.UNAUTHORIZED);
				}
				try {
					location.add(repositories, request.contentSource(), uploader);
				}
				catch (UnauthorizedUserException e) {
					return HttpResponse.status(HttpStatus.FORBIDDEN);
				}
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
