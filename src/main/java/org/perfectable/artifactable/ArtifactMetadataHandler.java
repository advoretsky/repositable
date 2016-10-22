package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class ArtifactMetadataHandler implements RequestHandler {
	private final Server server;

	public static ArtifactMetadataHandler of(Server server) {
		return new ArtifactMetadataHandler(server);
	}

	private ArtifactMetadataHandler(Server server) {
		this.server = server;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		ArtifactMetadataLocation location = ArtifactMetadataLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Metadata> metadata = server.find(location);
				if (!metadata.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return MetadataHttpResponse.of(metadata.get(), location.hashMethod);
			case PUT:
				// MARK metadata upload is ignored
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
