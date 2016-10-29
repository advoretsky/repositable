package org.perfectable.repositable;

import java.nio.file.Path;

public interface ArtifactIdentifier {
	Path asFilePath();

	boolean matches(Filter filter);
}
