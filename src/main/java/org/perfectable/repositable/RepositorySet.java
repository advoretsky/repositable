package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.util.Collection;

public interface RepositorySet {
	Collection<Metadata> listMetadata(MetadataIdentifier metadataIdentifier);

	Collection<Artifact> listArtifacts(ArtifactIdentifier artifactIdentifier);
}
