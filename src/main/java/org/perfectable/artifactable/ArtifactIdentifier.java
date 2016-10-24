package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class ArtifactIdentifier {
	private final String groupId;
	private final String artifactId;

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

	public String asFileBaseName(String versionBare) {
		return artifactId + "-" + versionBare;
	}

	public String asFileName(String version, Optional<String> classifier, String packaging) {
		String classifierSuffix = classifier.isPresent() ? "-" + classifier.get() : "";
		String fileName = artifactId + "-" + version + classifierSuffix + "." + packaging;
		return fileName;
	}

	public Metadata createEmptyMetadata() {
		Metadata metadata = new Metadata();
		metadata.setArtifactId(artifactId);
		metadata.setGroupId(groupId);
		return metadata;
	}

	public String asSnapshotFilename(String fullVersionWithExtension) {
		String filePath = artifactId + "-" + fullVersionWithExtension;
		return filePath;
	}
}
