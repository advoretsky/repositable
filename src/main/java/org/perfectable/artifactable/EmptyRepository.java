package org.perfectable.artifactable;

import org.perfectable.artifactable.authorization.User;
import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public final class EmptyRepository implements Repository {
	public static final EmptyRepository INSTANCE = new EmptyRepository();

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		return metadataIdentifier.createEmptyMetadata();
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier) {
		return Optional.empty();
	}

	@Override
	public void put(ArtifactIdentifier artifactIdentifier, Artifact artifact, User uploader) throws InsertionRejected {
		throw new InsertionRejected();
	}

	private EmptyRepository() {
		// singleton
	}
}
