package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public final class PackageIdentifier implements ArtifactIdentifier {
	private final VersionIdentifier versionIdentifier;
	private final Optional<String> classifier;
	private final String packaging;

	public static PackageIdentifier of(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		return new PackageIdentifier(versionIdentifier, classifier, packaging);
	}

	private PackageIdentifier(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		this.versionIdentifier = versionIdentifier;
		this.classifier = classifier;
		this.packaging = packaging;
	}

	public Path asSnapshotPath(LocalDateTime timestamp, int buildId) {
		return versionIdentifier.asSnapshotPath(classifier, packaging, timestamp, buildId);
	}

	public void addSnapshotVersion(Metadata metadata, LocalDateTime timestamp, int buildId) {
		versionIdentifier.addSnapshotVersion(metadata, classifier, packaging, timestamp, buildId);
	}

	@Override
	public Path asFilePath() {
		return versionIdentifier.asPackagePath(classifier, packaging);
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesPackage(versionIdentifier, classifier, packaging);
	}
}
