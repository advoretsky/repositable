package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Optional;

public final class FilteredRepository implements Repository {
	private final Repository wrapped;
	private final Filter filter;

	private FilteredRepository(Repository wrapped, Filter filter) {
		this.wrapped = wrapped;
		this.filter = filter;
	}

	public static FilteredRepository of(Repository repository, Filter filter) {
		return new FilteredRepository(repository, filter);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		if(!metadataIdentifier.matches(filter)) {
			return metadataIdentifier.createEmptyMetadata();
		}
		return wrapped.fetchMetadata(metadataIdentifier);
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier) {
		if(!artifactIdentifier.matches(filter)) {
			return Optional.empty();
		}
		return wrapped.findArtifact(artifactIdentifier);
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact content, User uploader, HashMethod hashMethod)
			throws UnauthorizedUserException, InsertionRejected {
		if(!identifier.matches(filter)) {
			throw new InsertionRejected();
		}
		wrapped.put(identifier, content, uploader, hashMethod);
	}

	@Override
	public Repository filtered(Filter filter) {
		return FilteredRepository.of(wrapped, this.filter.and(filter));
	}
}
