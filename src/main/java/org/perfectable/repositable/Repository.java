package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.authorization.UserSet;
import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.repositable.repository.AuthorizedRepository;
import org.perfectable.repositable.repository.FilteredRepository;

import java.util.Optional;

public interface Repository {
	Metadata fetchMetadata(MetadataIdentifier metadataIdentifier);

	Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier);

	void put(ArtifactIdentifier identifier, Artifact content, User uploader, HashMethod hashMethod)
			throws UnauthorizedUserException, InsertionRejected;

	default Repository restrictUploaders(UserSet newUploaders) {
		return AuthorizedRepository.of(this, newUploaders);
	}

	default Repository filtered(Filter newFilter) {
		return FilteredRepository.of(this, newFilter);
	}
}
