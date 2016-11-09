package org.perfectable.repositable;

import java.nio.file.Path;

public interface ArtifactIdentifier {
	Path asBasePath();

	Path asFetchPath(EntryLister lister);

	Path asUploadPath(BuildGenerator buildGenerator);

	boolean matches(Filter filter);

	interface BuildGenerator {
		SnapshotIdentifier generate(PackageIdentifier packageIdentifier);
	}
}
