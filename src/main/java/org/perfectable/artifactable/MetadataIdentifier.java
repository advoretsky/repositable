package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;

public interface MetadataIdentifier {
	Metadata createEmptyMetadata();

	Path asBasePath();

	Entry createEntry(Path versionPath);

	interface Entry {
		void appendVersion(Metadata metadata);
	}
}
