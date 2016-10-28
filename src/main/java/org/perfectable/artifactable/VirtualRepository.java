package org.perfectable.artifactable;

import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.artifactable.metadata.Metadata;

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
	public Optional<Metadata> findMetadata(MetadataIdentifier metadataIdentifier) {
		Collection<Metadata> sourceMetadataList = sources.listMetadata(metadataIdentifier);
		Optional<Metadata> resultOption = Optional.empty();
		for(Metadata sourceMetadata : sourceMetadataList) {
			if (!resultOption.isPresent()) {
				resultOption = Optional.of(sourceMetadata);
				continue;
			}
			Metadata result = resultOption.get();
			resultOption = Optional.of(result.merge(sourceMetadata));
		}
		return resultOption;
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
	public void put(ArtifactIdentifier identifier, Artifact content, User uploader) throws UnauthorizedUserException {
		// MARK throw exception
	}
}
