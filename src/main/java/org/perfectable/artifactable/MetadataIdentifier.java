package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;

public interface MetadataIdentifier {
	Metadata createEmptyMetadata();

	Path asBasePath();

	VersionEntry createVersionEntry(Path versionPath);

	interface VersionEntry {
		void appendVersion(Metadata metadata);
	}
}
