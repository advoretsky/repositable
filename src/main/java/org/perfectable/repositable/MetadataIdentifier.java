package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;

public interface MetadataIdentifier {
	Metadata createEmptyMetadata();

	Path asBasePath();

	Metadata createMetadata(Lister lister);

	interface Lister {
		interface Consumer {
			void entry(String name);
		}
		void list(Consumer consumer);
	}

	boolean matches(Filter filter);
}
