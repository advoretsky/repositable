package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.perfectable.artifactable.SnapshotIdentifier.TIMESTAMP_FORMATTER;


public final class VersionIdentifier implements FileIdentifier {
	private final ArtifactIdentifier artifactIdentifier;
	private final String versionBare;
	private final String versionModifier;
	private final String classifier;
	private final String packaging;

	public static VersionIdentifier of(ArtifactIdentifier artifactIdentifier, String versionBare, String versionModifier, String classifier, String packaging) {
		return new VersionIdentifier(artifactIdentifier, versionBare, versionModifier, classifier, packaging);
	}

	private VersionIdentifier(ArtifactIdentifier artifactIdentifier, String versionBare, String versionModifier, String classifier, String packaging) {
		this.artifactIdentifier = artifactIdentifier;
		this.versionBare = versionBare;
		this.classifier = classifier;
		this.versionModifier = versionModifier;
		this.packaging = packaging;
	}

	public VersionIdentifier withClassifier(String newClassifier) {
		return new VersionIdentifier(artifactIdentifier, versionBare, versionModifier, newClassifier, packaging);
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
		String versionModifier;
		if(fileName.endsWith("-SNAPSHOT")) {
			versionBare = fileName.replace("-SNAPSHOT$", "");
			versionModifier = "SNAPSHOT";
		}
		else {
			versionBare = fileName;
			versionModifier = null;
		}
		String classifier = null; // MARK
		String packaging = "pom"; // MARK
		return of(artifactIdentifier, versionBare, versionModifier, classifier, packaging);
	}

	public String completeVersion() {
		return (versionModifier == null) ? versionBare : (versionBare + "-" + versionModifier);
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
		String fileName = artifactIdentifier.asSnapshotFilename(versionBare, timestampString, buildId, classifier, packaging);
		return artifactPath.resolve(fileName);

	}

	public void addSnapshotVersion(Metadata metadata, LocalDateTime timestamp, String buildId) {
		String version = versionBare + "-" + timestamp.format(TIMESTAMP_FORMATTER) + "-" + buildId;
		metadata.addSnapshotVersion(packaging, version, timestamp);
	}
}
