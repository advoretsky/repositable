package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.artifactable.metadata.Version;
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
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		ArtifactIdentifier artifactIdentifier = location.artifactIdentifier; // MARK
		return selectedRepository.findMetadata(artifactIdentifier);
	}

	public Optional<Metadata> find(VersionMetadataLocation location) {
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		VersionIdentifier versionIdentifier = location.versionIdentifier; // MARK
		return selectedRepository.findMetadata(versionIdentifier);
	}

	public Optional<Artifact> find(SnapshotLocation location) {
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		SnapshotIdentifier artifactIdentifier = location.snapshotIdentifier; // MARK
		return selectedRepository.findArtifact(artifactIdentifier);
	}

	public Optional<Artifact> find(ReleaseLocation location) {
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		VersionIdentifier versionIdentifier = location.versionIdentifier; // MARK
		return selectedRepository.findArtifact(versionIdentifier);
	}

	private Optional<Repository> selectRepository(String repositoryName) {
		return repositories.stream()
			.filter(r -> repositoryName.equals(r.name))
			.findFirst();
	}

	public void add(SnapshotLocation location, ByteSource source) {
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return; // MARK return not found
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		SnapshotIdentifier snapshotIdentifier = location.snapshotIdentifier; // MARK
		Artifact artifact = Artifact.of(snapshotIdentifier, source);
		selectedRepository.put(artifact);
	}

	public void add(ReleaseLocation location, ByteSource source) {
		String repositoryName = location.repositoryName; // MARK
		Optional<Repository> selectedRepositoryOption = selectRepository(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return; // MARK return not found
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		VersionIdentifier releaseLocation = location.versionIdentifier; // MARK
		Artifact artifact = Artifact.of(releaseLocation, source);
		selectedRepository.put(artifact);
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
