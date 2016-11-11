package org.perfectable.repositable;

import org.perfectable.repositable.authorization.Authentication;
import org.perfectable.repositable.authorization.UnauthenticatedUserException;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactHandler implements RequestHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactHandler.class);

	private final RepositorySelector repositorySelector;
	private final Locator locator;

	public static ArtifactHandler of(RepositorySelector repositorySelector, Locator locator) {
		return new ArtifactHandler(repositorySelector, locator);
	}

	private ArtifactHandler(RepositorySelector repositorySelector, Locator locator) {
		this.repositorySelector = repositorySelector;
		this.locator = locator;
	}

	interface Locator {
		ArtifactLocation createLocation(String path);
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		ArtifactLocation location = locator.createLocation(path);
		switch (request.method()) {
			case GET:
				return handleRetrieval(location);
			case HEAD:
				return handleProbe(location);
			case PUT:
				return handleUpload(request, location);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	private HttpResponse handleRetrieval(ArtifactLocation location) {
		Optional<Artifact> artifactContent = location.find(repositorySelector);
		if (!artifactContent.isPresent()) {
			return HttpResponse.NOT_FOUND;
		}
		LOGGER.debug("Requested artifact content {}", location);
		Artifact artifact = artifactContent.get();
		HttpResponse response = ArtifactHttpResponse.of(artifact);
		return location.transformResponse(response);
	}

	private HttpResponse handleProbe(ArtifactLocation location) {
		Optional<Artifact> artifactHeaders = location.find(repositorySelector);
		if (!artifactHeaders.isPresent()) {
			return HttpResponse.NOT_FOUND;
		}
		LOGGER.debug("Requested artifact header {}", location);
		return HttpResponse.status(HttpStatus.OK);
	}

	private HttpResponse handleUpload(HttpRequest request, ArtifactLocation location) {
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
			location.add(repositorySelector, PublishedArtifact.of(request), uploader);
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
	}

	private static final class ArtifactHttpResponse implements HttpResponse {
		private final Artifact artifact;

		public static ArtifactHttpResponse of(Artifact artifact) {
			return new ArtifactHttpResponse(artifact);
		}

		private ArtifactHttpResponse(Artifact artifact) {
			this.artifact = artifact;
		}

		@Override
		public void writeTo(Writer writer) throws IOException {
			writer.setStatus(HttpStatus.OK);
			writer.setContentType(artifact.mediaType());
			try (OutputStream stream = writer.stream()) {
				artifact.writeContent(stream);
			}
		}
	}
}
