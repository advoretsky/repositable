package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class MetadataHandler implements RequestHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataHandler.class);

	private final Repositories repositories;
	private final Locator locator;

	public static MetadataHandler of(Repositories repositories, Locator locator) {
		return new MetadataHandler(repositories, locator);
	}

	private MetadataHandler(Repositories repositories, Locator locator) {
		this.repositories = repositories;
		this.locator = locator;
	}

	interface Locator {
		MetadataLocation createLocation(String path);
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		MetadataLocation location = locator.createLocation(path);
		switch(request.method()) {
			case GET:
				Optional<Metadata> metadata = location.find(repositories);
				if (!metadata.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				LOGGER.debug("Requested metadata {}", location);
				return location.createResponse(metadata.get());
			case PUT:
				LOGGER.debug("Ignored upload of metadata {}", location);
				// MARK metadata upload is ignored
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
