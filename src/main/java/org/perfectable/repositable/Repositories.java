package org.perfectable.repositable;

import com.google.common.collect.ImmutableSortedMap;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Repositories implements RepositorySelector, RepositorySet {
	private final ImmutableSortedMap<String, Repository> repositoryByName;

	public static Repositories create() {
		return new Repositories(ImmutableSortedMap.of());
	}

	private Repositories(ImmutableSortedMap<String, Repository> repositoryByName) {
		this.repositoryByName = repositoryByName;
	}

	public Repositories withAdditional(String name, Repository repository) {
		ImmutableSortedMap<String, Repository> newRepositoryByName =
				ImmutableSortedMap.<String, Repository>naturalOrder()
						.putAll(repositoryByName)
						.put(name, repository)
						.build();
		return new Repositories(newRepositoryByName);
	}

	@Override
	public Repository select(String repositoryName) {
		Repository repository = repositoryByName.get(repositoryName);
		if (repository == null) {
			return EmptyRepository.INSTANCE;
		}
		return repository;
	}

	@Override
	public Collection<Metadata> listMetadata(MetadataIdentifier metadataIdentifier) {
		return repositoryByName.values().stream()
				.map(repository -> repository.fetchMetadata(metadataIdentifier))
				.collect(Collectors.toSet());
	}

	@Override
	public Collection<Artifact> listArtifacts(ArtifactIdentifier artifactIdentifier) {
		Function<Repository, Optional<Artifact>> transformation = repository -> repository.findArtifact(artifactIdentifier);
		return repositoryByName.values().stream()
				.map(transformation)
				.flatMap(candidate -> candidate.isPresent() ? Stream.of(candidate.get()) : Stream.empty())
				.collect(Collectors.toList());
	}

}
