package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				return handleRetrieval(location);
			case PUT:
				return handleUpload(location);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	private HttpResponse handleRetrieval(MetadataLocation location) {
		Metadata metadata = location.fetch(repositories);
		LOGGER.debug("Requested metadata {}", location);
		return location.createResponse(metadata);
	}

	private HttpResponse handleUpload(MetadataLocation location) {
		LOGGER.debug("Ignored upload of metadata {}", location);
		// MARK metadata upload is ignored
		return HttpResponse.status(HttpStatus.OK);
	}
}
