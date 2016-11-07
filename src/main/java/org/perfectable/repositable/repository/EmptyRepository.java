package org.perfectable.repositable.repository;

import org.perfectable.repositable.Artifact;
import org.perfectable.repositable.ArtifactIdentifier;
import org.perfectable.repositable.HashMethod;
import org.perfectable.repositable.InsertionRejected;
import org.perfectable.repositable.MetadataIdentifier;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

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
	public void put(ArtifactIdentifier artifactIdentifier, Artifact artifact, User uploader, HashMethod hashMethod)
			throws InsertionRejected {
		throw new InsertionRejected();
	}

	private EmptyRepository() {
		// singleton
	}
}
