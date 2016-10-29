package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Optional;

public interface Repository {
	Metadata fetchMetadata(MetadataIdentifier metadataIdentifier);

	Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier);

	void put(ArtifactIdentifier identifier, Artifact content, User uploader) throws UnauthorizedUserException, InsertionRejected;

}