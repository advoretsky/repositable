package org.perfectable.artifactable;

import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public interface Repository {
	Metadata fetchMetadata(MetadataIdentifier metadataIdentifier);

	Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier);

	void put(ArtifactIdentifier identifier, Artifact content, User uploader) throws UnauthorizedUserException;

}
