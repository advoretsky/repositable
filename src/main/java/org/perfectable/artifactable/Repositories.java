package org.perfectable.artifactable;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public final class Repositories {
	private final ImmutableMap<String, FileRepository> repositoryByName;

	public static Repositories create() {
		return new Repositories(ImmutableMap.of());
	}

	private Repositories(ImmutableMap<String, FileRepository> repositoryByName) {
		this.repositoryByName = repositoryByName;
	}

	public Repositories withAdditional(String name, FileRepository repository) {
		ImmutableMap<String, FileRepository> newRepositoryByName = ImmutableMap.<String, FileRepository>builder()
				.putAll(repositoryByName)
				.put(name, repository)
				.build();
		return new Repositories(newRepositoryByName);
	}

	public Optional<Metadata> findMetadata(String repositoryName, ArtifactIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findMetadata(artifactIdentifier);
	}

	public Optional<Metadata> findMetadata(String repositoryName, VersionIdentifier versionIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findMetadata(versionIdentifier);
	}

	public Optional<Artifact> findArtifact(String repositoryName, VersionIdentifier versionIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findArtifact(versionIdentifier);
	}

	public Optional<Artifact> findArtifact(String repositoryName, SnapshotIdentifier snapshotIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findArtifact(snapshotIdentifier);
	}

	public void addSnapshot(String repositoryName, SnapshotIdentifier snapshotIdentifier, ByteSource source) {
		Repository selectedRepository = selectByName(repositoryName);
		Artifact artifact = Artifact.of(snapshotIdentifier, source);
		selectedRepository.put(artifact);
	}

	public void addRelease(String repositoryName, VersionIdentifier versionIdentifier, ByteSource source) {
		Repository selectedRepository = selectByName(repositoryName);
		Artifact artifact = Artifact.of(versionIdentifier, source);
		selectedRepository.put(artifact);
	}

	private Repository selectByName(String repositoryName) {
		FileRepository repository = repositoryByName.get(repositoryName);
		if(repository == null) {
			return EmptyRepository.INSTANCE;
		}
		return repository;
	}
}
