package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;

public final class EmptyRepository implements Repository {
	public static final EmptyRepository INSTANCE = new EmptyRepository();

	@Override
	public Optional<Metadata> findMetadata(ArtifactIdentifier artifactIdentifier) {
		return Optional.empty();
	}

	@Override
	public Optional<Metadata> findMetadata(VersionIdentifier versionIdentifier) {
		return Optional.empty();
	}

	@Override
	public Optional<Artifact> findArtifact(SnapshotIdentifier snapshotIdentifier) {
		return Optional.empty();
	}

	@Override
	public Optional<Artifact> findArtifact(VersionIdentifier releaseIdentifier) {
		return Optional.empty();
	}

	@Override
	public void put(Artifact artifact) {
		// MARK disable uploading
	}

	private EmptyRepository() {
		// singleton
	}
}
