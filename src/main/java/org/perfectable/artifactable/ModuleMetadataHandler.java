package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class ModuleMetadataHandler implements RequestHandler {
	private final Repositories repositories;

	public static ModuleMetadataHandler of(Repositories repositories) {
		return new ModuleMetadataHandler(repositories);
	}

	private ModuleMetadataHandler(Repositories repositories) {
		this.repositories = repositories;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		ModuleMetadataLocation location = ModuleMetadataLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Metadata> metadata = location.find(repositories);
				if (!metadata.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return location.createResponse(metadata.get());
			case PUT:
				// MARK metadata upload is ignored
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
