package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

public final class SnapshotHandler implements RequestHandler {
	private final Repositories repositories;

	public static SnapshotHandler of(Repositories repositories) {
		return new SnapshotHandler(repositories);
	}

	private SnapshotHandler(Repositories repositories) {
		this.repositories = repositories;
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		String path = request.completePath();
		SnapshotLocation location = SnapshotLocation.fromPath(path);
		switch(request.method()) {
			case GET:
				Optional<Artifact> artifact = location.find(repositories);
				if(!artifact.isPresent()) {
					return HttpResponse.NOT_FOUND;
				}
				return location.createResponse(artifact.get());
			case PUT:
				location.add(repositories, request.contentSource());
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
