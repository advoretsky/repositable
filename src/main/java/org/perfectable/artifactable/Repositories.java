package org.perfectable.artifactable;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public final class Repositories {
	private final ImmutableMap<String, Repository> repositoryByName;

	public static Repositories create() {
		return new Repositories(ImmutableMap.of());
	}

	private Repositories(ImmutableMap<String, Repository> repositoryByName) {
		this.repositoryByName = repositoryByName;
	}

	public Repositories withAdditional(String name, Repository repository) {
		ImmutableMap<String, Repository> newRepositoryByName = ImmutableMap.<String, Repository>builder()
				.putAll(repositoryByName)
				.put(name, repository)
				.build();
		return new Repositories(newRepositoryByName);
	}

	public Optional<Metadata> findMetadata(String repositoryName, ArtifactIdentifier artifactIdentifier) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		return selectedRepository.findMetadata(artifactIdentifier);
	}

	public Optional<Metadata> findMetadata(String repositoryName, VersionIdentifier versionIdentifier) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		return selectedRepository.findMetadata(versionIdentifier);
	}

	public Optional<Artifact> findArtifact(String repositoryName, VersionIdentifier versionIdentifier) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		return selectedRepository.findArtifact(versionIdentifier);
	}

	public Optional<Artifact> findArtifact(String repositoryName, SnapshotIdentifier snapshotIdentifier) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		return selectedRepository.findArtifact(snapshotIdentifier);
	}

	public void addSnapshot(String repositoryName, SnapshotIdentifier snapshotIdentifier, ByteSource source) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return; // MARK return not found
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		Artifact artifact = Artifact.of(snapshotIdentifier, source);
		selectedRepository.put(artifact);
	}

	public void addRelease(String repositoryName, VersionIdentifier versionIdentifier, ByteSource source) {
		Optional<Repository> selectedRepositoryOption = selectByName(repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return; // MARK return not found
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		Artifact artifact = Artifact.of(versionIdentifier, source);
		selectedRepository.put(artifact);
	}


	private Optional<Repository> selectByName(String repositoryName) {
		return Optional.ofNullable(repositoryByName.get(repositoryName));
	}
}
