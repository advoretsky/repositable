package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.perfectable.artifactable.SnapshotIdentifier.TIMESTAMP_FORMATTER;


public final class VersionIdentifier implements FileIdentifier {
	private final ArtifactIdentifier artifactIdentifier;
	private final String versionBare;
	private final Optional<String> versionModifier;
	private final Optional<String> classifier;
	private final String packaging;

	public static VersionIdentifier of(ArtifactIdentifier artifactIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		return new VersionIdentifier(artifactIdentifier, versionBare, versionModifier, classifier, packaging);
	}

	private VersionIdentifier(ArtifactIdentifier artifactIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		this.artifactIdentifier = artifactIdentifier;
		this.versionBare = versionBare;
		this.classifier = classifier;
		this.versionModifier = versionModifier;
		this.packaging = packaging;
	}

	public VersionIdentifier withClassifier(String newClassifier) {
		return new VersionIdentifier(artifactIdentifier, versionBare, versionModifier, Optional.of(newClassifier), packaging);
	}

	public VersionIdentifier withPackaging(String newPackaging) {
		return new VersionIdentifier(artifactIdentifier, versionBare, versionModifier, classifier, newPackaging);
	}

	public Path asBasePath() {
		Path artifactPath = artifactIdentifier.asBasePath();
		String version = completeVersion();
		return artifactPath.resolve(version);
	}

	@Override
	public Path asFilePath() {
		Path versionPath = asBasePath();
		String version = completeVersion();
		String fileName = artifactIdentifier.asFileName(version, classifier, packaging);
		return versionPath.resolve(fileName);
	}

	public static VersionIdentifier ofEntry(ArtifactIdentifier artifactIdentifier, Path versionPath) {
		Path entryPath = versionPath.subpath(1,versionPath.getNameCount());
		entryPath = artifactIdentifier.asBasePath().relativize(entryPath);
		String fileName = entryPath.getFileName().toString();
		String versionBare;
		Optional<String> versionModifier;
		if(fileName.endsWith("-SNAPSHOT")) {
			versionBare = fileName.replace("-SNAPSHOT$", "");
			versionModifier = Optional.of("SNAPSHOT");
		}
		else {
			versionBare = fileName;
			versionModifier = Optional.empty();
		}
		return of(artifactIdentifier, versionBare, versionModifier, null, "pom");
	}

	public String completeVersion() {
		return versionModifier.isPresent() ? (versionBare + "-" + versionModifier.get()) : versionBare;
	}

	public String fileBaseName() {
		return artifactIdentifier.asFileBaseName(versionBare);
	}

	public Metadata createEmptyMetadata() {
		Metadata metadata = artifactIdentifier.createEmptyMetadata();
		metadata.setMainVersion(completeVersion());
		return metadata;
	}

	public Path asSnapshotPath(LocalDateTime timestamp, String buildId) {
		String timestampString = TIMESTAMP_FORMATTER.format(timestamp);
		Path artifactPath = asBasePath();
		String classifierSuffix = classifier == null ? "" : "-" + classifier;
		String fullVersion = versionBare + "-" + timestampString + "-" + buildId + classifierSuffix + "." + packaging;
		String fileName = artifactIdentifier.asSnapshotFilename(fullVersion);
		return artifactPath.resolve(fileName);

	}

	public void addSnapshotVersion(Metadata metadata, LocalDateTime timestamp, String buildId) {
		String version = versionBare + "-" + timestamp.format(TIMESTAMP_FORMATTER) + "-" + buildId;
		metadata.addSnapshotVersion(packaging, version, timestamp);
	}
}
