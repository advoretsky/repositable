package org.perfectable.repositable;

import org.perfectable.repositable.authorization.Authentication;
import org.perfectable.repositable.authorization.UnauthenticatedUserException;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ArtifactHandler implements RequestHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactHandler.class);

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
				Optional<Artifact> artifactContent = location.find(repositories);
				if(!artifactContent.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				LOGGER.debug("Requested artifact content {}", location);
				return location.createResponse(artifactContent.get());
			case HEAD:
				Optional<Artifact> artifactHeaders = location.find(repositories);
				if(!artifactHeaders.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				LOGGER.debug("Requested artifact header {}", location);
				return HttpResponse.status(HttpStatus.OK);
			case PUT:
				Authentication authentication = request.select(Authentication.ATTRIBUTE).get();
				User uploader;
				try {
					uploader = authentication.requireUser();
				}
				catch (UnauthenticatedUserException e) {
					LOGGER.info("Unauthenticated user tried to upload {}", location);
					return HttpResponse.status(HttpStatus.UNAUTHORIZED);
				}
				try {
					location.add(repositories, request.contentSource(), uploader);
				}
				catch (UnauthorizedUserException e) {
					LOGGER.info("Not allowed user {} tried to upload {}", uploader, location);
					return HttpResponse.status(HttpStatus.FORBIDDEN);
				}
				catch (InsertionRejected insertionRejected) {
					LOGGER.info("User {} tried to upload {} to filtered repository", uploader, location);
					return HttpResponse.status(HttpStatus.FORBIDDEN);
				}
				LOGGER.info("User {} uploaded {}", uploader, location);
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}