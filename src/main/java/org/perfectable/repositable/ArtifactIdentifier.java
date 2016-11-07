package org.perfectable.repositable;

import java.nio.file.Path;

public interface ArtifactIdentifier {
	Path asBasePath();

	Path asFetchPath(EntryLister lister);

	Path asUploadPath();

	boolean matches(Filter filter);
}
