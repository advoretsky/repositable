package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

public final class ServerHandler implements RequestHandler {
	private final Server server;

	public static ServerHandler of(Server server) {
		return new ServerHandler(server);
	}

	private ServerHandler(Server server) {
		this.server = server;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		ArtifactLocation location = ArtifactLocation.fromPath(request.completePath());
		switch(request.method()) {
			case GET:
				Artifact artifact = server.find(location);
				return ArtifactHttpResponse.of(artifact);
			case POST:
				server.add(location, request.content());
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
