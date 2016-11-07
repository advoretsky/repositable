package org.perfectable.repositable.repository;

import org.perfectable.repositable.Artifact;
import org.perfectable.repositable.ArtifactIdentifier;
import org.perfectable.repositable.HashMethod;
import org.perfectable.repositable.InsertionRejected;
import org.perfectable.repositable.MetadataIdentifier;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.RepositorySet;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Collection;
import java.util.Optional;

public final class VirtualRepository implements Repository {
	private final RepositorySet sources;

	private VirtualRepository(RepositorySet sources) {
		this.sources = sources;
	}

	public static VirtualRepository create(RepositorySet sources) {
		return new VirtualRepository(sources);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		Collection<Metadata> sourceMetadataList = sources.listMetadata(metadataIdentifier);
		Metadata result = metadataIdentifier.createEmptyMetadata();
		for(Metadata sourceMetadata : sourceMetadataList) {
			result = result.merge(sourceMetadata);
		}
		return result;
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier) {
		Collection<Artifact> artifacts = sources.listArtifacts(artifactIdentifier);
		if(artifacts.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(artifacts.iterator().next());
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact content, User uploader, HashMethod hashMethod)
			throws InsertionRejected {
		throw new InsertionRejected();
	}
}
