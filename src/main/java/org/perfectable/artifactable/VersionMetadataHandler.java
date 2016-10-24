package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class VersionMetadataHandler implements RequestHandler {
	private final Server server;

	public static VersionMetadataHandler of(Server server) {
		return new VersionMetadataHandler(server);
	}

	private VersionMetadataHandler(Server server) {
		this.server = server;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		VersionMetadataLocation location = VersionMetadataLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Metadata> metadata = server.find(location);
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
