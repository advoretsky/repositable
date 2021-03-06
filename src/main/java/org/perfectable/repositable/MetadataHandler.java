package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MetadataHandler implements RequestHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataHandler.class);

	private final RepositorySelector repositorySelector;
	private final Locator locator;

	public static MetadataHandler of(RepositorySelector repositorySelector, Locator locator) {
		return new MetadataHandler(repositorySelector, locator);
	}

	private MetadataHandler(RepositorySelector repositorySelector, Locator locator) {
		this.repositorySelector = repositorySelector;
		this.locator = locator;
	}

	interface Locator {
		MetadataLocation createLocation(String path);
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		MetadataLocation location = locator.createLocation(path);
		switch (request.method()) {
			case GET:
				return handleRetrieval(location);
			case PUT:
				return handleUpload(location);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	private HttpResponse handleRetrieval(MetadataLocation location) {
		Metadata metadata = location.fetch(repositorySelector);
		LOGGER.debug("Requested metadata {}", location);
		if (metadata.isEmpty()) {
			return HttpResponse.NOT_FOUND;
		}
		HttpResponse response = HttpResponse.OK
				.withContentWriter(MediaType.XML_UTF_8, metadata::writeInto);
		return location.transformResponse(response);
	}

	private HttpResponse handleUpload(MetadataLocation location) {
		LOGGER.debug("Ignored upload of metadata {}", location);
		// metadata upload is ignored
		return HttpResponse.status(HttpStatus.OK);
	}
}
