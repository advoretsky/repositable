package org.perfectable.artifactable;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
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

	public Optional<Metadata> findMetadata(String repositoryName, MetadataIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findMetadata(artifactIdentifier);
	}

	public Optional<Artifact> findArtifact(String repositoryName, ArtifactIdentifier versionIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findArtifact(versionIdentifier);
	}

	public void add(String repositoryName, ArtifactIdentifier artifactIdentifier, ByteSource source, User uploader)
			throws UnauthorizedUserException {
		Repository selectedRepository = selectByName(repositoryName);
		selectedRepository.put(artifactIdentifier, Artifact.of(source), uploader);
	}

	private Repository selectByName(String repositoryName) {
		FileRepository repository = repositoryByName.get(repositoryName);
		if(repository == null) {
			return EmptyRepository.INSTANCE;
		}
		return repository;
	}
}
