package org.perfectable.artifactable;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ArtifactIdentifier {
	String groupId;
	String artifactId;

	public static ArtifactIdentifier of(String groupId, String artifactId) {
		return new ArtifactIdentifier(groupId, artifactId);
	}

	private ArtifactIdentifier(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public Path asBasePath() {
		String groupPath = groupId.replace(".", "/");
		return Paths.get(groupPath, artifactId);
	}
}
