package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class ReleaseHandler implements RequestHandler {
	private final Server server;

	public static ReleaseHandler of(Server server) {
		return new ReleaseHandler(server);
	}

	private ReleaseHandler(Server server) {
		this.server = server;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		ReleaseLocation location = ReleaseLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Artifact> artifact = server.find(location);
				if(!artifact.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return location.createResponse(artifact.get());
			case PUT:
				if(location.allowsAdding()) {
					server.add(location, request.contentSource());
				}
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
