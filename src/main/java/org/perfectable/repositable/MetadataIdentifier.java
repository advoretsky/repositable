package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;

public interface MetadataIdentifier {
	Metadata createEmptyMetadata();

	Path asBasePath();

	Metadata createMetadata(EntryLister lister);

	boolean matches(Filter filter);
}
