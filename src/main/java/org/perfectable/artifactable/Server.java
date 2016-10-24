package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.WebApplication;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Optional;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "server")
public class Server {
	@XmlElement(name = "port", required = true)
	private int port;

	@XmlElement(name = "repository")
	private List<Repository> repositories;

	public Optional<Metadata> find(ArtifactMetadataLocation location) {
		return location.find(repositories);
	}

	public Optional<Metadata> find(VersionMetadataLocation location) {
		return location.find(repositories);
	}

	public Optional<Artifact> find(SnapshotLocation location) {
		return location.find(repositories);
	}

	public Optional<Artifact> find(ReleaseLocation location) {
		return location.find(repositories);
	}

	public void add(SnapshotLocation location, ByteSource source) {
		location.add(repositories, source);
	}

	public void add(ReleaseLocation location, ByteSource source) {
		location.add(repositories, source);
	}

	public void serve() {
		WebApplication.begin()
				.withPort(port)
				.withHandler(VersionMetadataLocation.PATH_PATTERN, VersionMetadataHandler.of(this))
				.withHandler(ArtifactMetadataLocation.PATH_PATTERN, ArtifactMetadataHandler.of(this))
				.withHandler(ReleaseLocation.PATH_PATTERN, ReleaseHandler.of(this))
				.withHandler(SnapshotLocation.PATH_PATTERN, SnapshotHandler.of(this))
				.withRootHandler(RequestHandler.constant(HttpResponse.NOT_FOUND))
				.serveBlocking();
	}
}
