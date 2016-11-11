package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public final class PackageIdentifier implements ArtifactIdentifier {
	private final VersionIdentifier versionIdentifier;
	private final Optional<String> classifier;
	private final String packaging;

	public static PackageIdentifier of(VersionIdentifier versionIdentifier, Optional<String> classifier,
									   String packaging) {
		return new PackageIdentifier(versionIdentifier, classifier, packaging);
	}

	private PackageIdentifier(VersionIdentifier versionIdentifier, Optional<String> classifier, String packaging) {
		this.versionIdentifier = versionIdentifier;
		this.classifier = classifier;
		this.packaging = packaging;
	}

	@Override
	public Path asBasePath() {
		return versionIdentifier.asBasePath();
	}

	public Path asBuildPath(LocalDateTime timestamp, int buildId) {
		return versionIdentifier.asBuildPath(classifier, packaging, timestamp, buildId);
	}

	public Path asArtifactPath() {
		return versionIdentifier.asArtifactPath(classifier, packaging);
	}

	@Override
	public Path asFetchPath(EntryLister lister) {
		return versionIdentifier.asFetchPath(lister, classifier, packaging);
	}

	@Override
	public Path asUploadPath(BuildGenerator buildGenerator) {
		return versionIdentifier.asUploadPath(buildGenerator, classifier, packaging);
	}

	public void addSnapshotVersion(Metadata metadata, LocalDateTime timestamp, int buildId) {
		versionIdentifier.addSnapshotVersion(metadata, classifier, packaging, timestamp, buildId);
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesPackage(versionIdentifier, classifier, packaging);
	}
}
