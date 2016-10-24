package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public interface Repository {
	Optional<Metadata> findMetadata(MetadataIdentifier metadataIdentifier);

	Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier);

	void put(ArtifactIdentifier identifier, Artifact content);

}
