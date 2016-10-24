package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public interface Repository {
	Optional<Metadata> findMetadata(ArtifactIdentifier artifactIdentifier);

	Optional<Metadata> findMetadata(VersionIdentifier versionIdentifier);

	Optional<Artifact> findArtifact(SnapshotIdentifier snapshotIdentifier);

	Optional<Artifact> findArtifact(VersionIdentifier releaseIdentifier);

	void put(Artifact artifact);

}
