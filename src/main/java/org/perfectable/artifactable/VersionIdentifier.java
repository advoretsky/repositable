package org.perfectable.artifactable;

import java.nio.file.Path;


public final class VersionIdentifier implements FileIdentifier {
	final ArtifactIdentifier artifactIdentifier;
	final String versionBare;
	final String versionModifier;
	final String classifier;
	final String packaging;

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
		String classifierSuffix = classifier == null ? "" : "-" + classifier;
		String fileName = artifactIdentifier.artifactId + "-" + version + classifierSuffix + "." + packaging;
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
}
