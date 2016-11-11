package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.perfectable.repositable.SnapshotIdentifier.TIMESTAMP_FORMATTER;

public final class ModuleIdentifier implements MetadataIdentifier {
	static final char CLASSIFIER_SEPARATOR = '-';
	static final char VERSION_SEPARATOR = '-';
	static final char TIMESTAMP_SEPARATOR = '-';
	static final char BUILD_ID_SEPARATOR = '-';
	static final char PACKAGING_SEPARATOR = '.';

	private final String groupId;
	private final String artifactId;

	public static ModuleIdentifier of(String groupId, String artifactId) {
		return new ModuleIdentifier(groupId, artifactId);
	}

	private ModuleIdentifier(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	@Override
	public Metadata createEmptyMetadata() {
		Metadata metadata = new Metadata();
		metadata.setArtifactId(artifactId);
		metadata.setGroupId(groupId);
		return metadata;
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
	public Path asBasePath() {
		String groupPath = groupId.replace(".", "/");
		return Paths.get(groupPath, artifactId);
	}

	public String asFileBaseName(String versionBare) {
		return artifactId + VERSION_SEPARATOR + versionBare;
	}

	public String asFileName(String version, Optional<String> classifier, String packaging) {
		String classifierSuffix = classifier.isPresent() ? CLASSIFIER_SEPARATOR + classifier.get() : "";
		return artifactId + VERSION_SEPARATOR + version + classifierSuffix + PACKAGING_SEPARATOR + packaging;
	}

	public String asSnapshotFilename(String versionBare, Optional<String> classifier, String packaging,
									 LocalDateTime timestamp, int buildId) {
		String timestampString = TIMESTAMP_FORMATTER.format(timestamp);
		String classifierSuffix = classifier.isPresent() ? CLASSIFIER_SEPARATOR + classifier.get() : "";
		return artifactId + VERSION_SEPARATOR + versionBare
				+ TIMESTAMP_SEPARATOR + timestampString
				+ BUILD_ID_SEPARATOR + buildId + classifierSuffix + PACKAGING_SEPARATOR + packaging;
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesModule(groupId, artifactId);
	}
}
