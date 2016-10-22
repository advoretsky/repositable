package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;
import org.perfectable.webable.handler.RequestHandler;

import java.util.Optional;

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
		String path = request.completePath();
		switch(request.method()) {
			case GET:
				if (VersionMetadataLocation.matchesPath(path)) {
					VersionMetadataLocation location = VersionMetadataLocation.fromPath(path);
					Optional<Metadata> metadata = server.find(location);
					if (!metadata.isPresent()) {
						return HttpResponse.NOT_FOUND;
					}
					return MetadataHttpResponse.of(metadata.get(), location.hashMethod);
				}
				else if(SnapshotLocation.matchesPath(path)) {
					SnapshotLocation location = SnapshotLocation.fromPath(path);
					Optional<Artifact> artifact = server.find(location);
					if(!artifact.isPresent()) {
						return HttpResponse.NOT_FOUND;
					}
					return ArtifactHttpResponse.of(artifact.get(), location.hashMethod);
				}
				return HttpResponse.NOT_FOUND;
			case PUT:
				SnapshotLocation location = SnapshotLocation.fromPath(path);
				server.add(location, request.contentSource());
				return HttpResponse.status(HttpStatus.OK);
			default:
				return HttpResponse.status(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}
}
