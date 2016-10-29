package org.perfectable.repositable;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Collection;
import java.util.Optional;
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

	public Metadata fetchMetadata(String repositoryName, MetadataIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.fetchMetadata(artifactIdentifier);
	}

	public Collection<Metadata> listMetadata(MetadataIdentifier metadataIdentifier) {
		return repositoryByName.values().stream()
				.map(repository -> repository.fetchMetadata(metadataIdentifier))
				.collect(Collectors.toSet());
	}

	public Optional<Artifact> findArtifact(String repositoryName, ArtifactIdentifier artifactIdentifier) {
		Repository selectedRepository = selectByName(repositoryName);
		return selectedRepository.findArtifact(artifactIdentifier);
	}

	public Collection<Artifact> listArtifacts(ArtifactIdentifier artifactIdentifier) {
		Function<Repository, Optional<Artifact>> transformation = repository -> repository.findArtifact(artifactIdentifier);
		return repositoryByName.values().stream()
				.map(transformation)
				.flatMap(candidate -> candidate.isPresent() ? Stream.of(candidate.get()) : Stream.empty())
				.collect(Collectors.toSet());
	}

	public void add(String repositoryName, ArtifactIdentifier artifactIdentifier, ByteSource source, User uploader)
			throws UnauthorizedUserException, InsertionRejected {
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

}
