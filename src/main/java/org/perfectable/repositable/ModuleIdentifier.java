package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class ModuleIdentifier implements MetadataIdentifier {
	private final String groupId;
	private final String artifactId;

	public static ModuleIdentifier of(String groupId, String artifactId) {
		return new ModuleIdentifier(groupId, artifactId);
	}

	private ModuleIdentifier(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public Path asBasePath() {
		String groupPath = groupId.replace(".", "/");
		return Paths.get(groupPath, artifactId);
	}

	@Override
	public Metadata createMetadata(EntryLister lister) {
		Metadata metadata = createEmptyMetadata();
		lister.list(element -> {
			VersionIdentifier versionIdentifier = VersionIdentifier.ofEntry(this, element);
			versionIdentifier.appendVersion(metadata);
		});
		return metadata;
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesModule(groupId, artifactId);
	}

	public String asFileBaseName(String versionBare) {
		return artifactId + "-" + versionBare;
	}

	public String asFileName(String version, Optional<String> classifier, String packaging) {
		String classifierSuffix = classifier.isPresent() ? "-" + classifier.get() : "";
		String fileName = artifactId + "-" + version + classifierSuffix + "." + packaging;
		return fileName;
	}

	public String asSnapshotFilename(String fullVersionWithExtension) {
		String filePath = artifactId + "-" + fullVersionWithExtension;
		return filePath;
	}

	public Metadata createEmptyMetadata() {
		Metadata metadata = new Metadata();
		metadata.setArtifactId(artifactId);
		metadata.setGroupId(groupId);
		return metadata;
	}
}
