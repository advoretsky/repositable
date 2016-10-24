package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public final class EmptyRepository implements Repository {
	public static final EmptyRepository INSTANCE = new EmptyRepository();

	@Override
	public Optional<Metadata> findMetadata(MetadataIdentifier metadataIdentifier) {
		return Optional.empty();
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier) {
		return Optional.empty();
	}

	@Override
	public void put(ArtifactIdentifier artifactIdentifier, Artifact artifact) {
		// MARK disable uploading
	}

	private EmptyRepository() {
		// singleton
	}
}