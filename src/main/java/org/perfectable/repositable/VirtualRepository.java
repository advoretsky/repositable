package org.perfectable.repositable;

import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Collection;
import java.util.Optional;

public final class VirtualRepository implements Repository {
	private final Repositories sources;

	private VirtualRepository(Repositories sources) {
		this.sources = sources;
	}

	public static VirtualRepository create(Repositories sources) {
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
	public void put(ArtifactIdentifier identifier, Artifact content, User uploader) throws InsertionRejected {
		throw new InsertionRejected();
	}
}
