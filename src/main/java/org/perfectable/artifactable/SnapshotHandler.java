package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class SnapshotHandler implements RequestHandler {
	private final Server server;

	public static SnapshotHandler of(Server server) {
		return new SnapshotHandler(server);
	}

	private SnapshotHandler(Server server) {
		this.server = server;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		SnapshotLocation location = SnapshotLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Artifact> artifact = server.find(location);
				if(!artifact.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return ArtifactHttpResponse.of(artifact.get(), location.hashMethod);
			case PUT:
				if(HashMethod.NONE.equals(location.hashMethod)) {
					server.add(location, request.contentSource());
				}
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
