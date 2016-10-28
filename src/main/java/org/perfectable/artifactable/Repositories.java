package org.perfectable.artifactable;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.artifactable.metadata.Metadata;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Optional<Metadata> findMetadata(String repositoryName, MetadataIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findMetadata(artifactIdentifier);
	}

	public Collection<Metadata> listMetadata(MetadataIdentifier metadataIdentifier) {
		return collect(repository -> repository.findMetadata(metadataIdentifier));
	}

	public Optional<Artifact> findArtifact(String repositoryName, ArtifactIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findArtifact(artifactIdentifier);
	}

	public Collection<Artifact> listArtifacts(ArtifactIdentifier artifactIdentifier) {
		return collect(repository -> repository.findArtifact(artifactIdentifier));
	}

	public void add(String repositoryName, ArtifactIdentifier artifactIdentifier, ByteSource source, User uploader)
			throws UnauthorizedUserException {
		Repository selectedRepository = selectByName(repositoryName);
		selectedRepository.put(artifactIdentifier, Artifact.of(source), uploader);
	}

	private Repository selectByName(String repositoryName) {
		Repository repository = repositoryByName.get(repositoryName);
		if(repository == null) {
			return EmptyRepository.INSTANCE;
		}
		return repository;
	}

	private <T> Set<T> collect(Function<Repository, Optional<T>> transformation) {
		return repositoryByName.values().stream()
				.map(transformation)
				.flatMap(candidate -> candidate.isPresent() ? Stream.of(candidate.get()) : Stream.empty())
				.collect(Collectors.toSet());
	}

}
