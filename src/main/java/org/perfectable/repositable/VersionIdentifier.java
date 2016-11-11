package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.perfectable.repositable.SnapshotIdentifier.TIMESTAMP_FORMATTER;

public final class VersionIdentifier implements MetadataIdentifier {
	private static final char QUALIFIER_SEPARATOR = '-';
	private final ModuleIdentifier moduleIdentifier;
	private final String versionBare;
	private final Optional<String> versionQualifier;

	public static VersionIdentifier of(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier) {
		return new VersionIdentifier(moduleIdentifier, versionBare, versionQualifier);
	}

	public static VersionIdentifier ofEntry(ModuleIdentifier moduleIdentifier, String entry) {
		String versionBare;
		Optional<String> versionQualifier;
		int qualifierStart = entry.indexOf(QUALIFIER_SEPARATOR);
		if(qualifierStart >= 0) {
			versionBare = entry.substring(0, qualifierStart);
			versionQualifier = Optional.of(entry.substring(qualifierStart));
		}
		else {
			versionBare = entry;
			versionQualifier = Optional.empty();
		}
		return of(moduleIdentifier, versionBare, versionQualifier);
	}

	private VersionIdentifier(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier) {
		this.moduleIdentifier = moduleIdentifier;
		this.versionBare = versionBare;
		this.versionQualifier = versionQualifier;
	}

	@Override
	public Metadata createEmptyMetadata() {
		Metadata metadata = moduleIdentifier.createEmptyMetadata();
		metadata.setVersion(completeVersion());
		return metadata;
	}

	@Override
	public Metadata createMetadata(EntryLister lister) {
		Metadata metadata = createEmptyMetadata();
		lister.list(element -> {
			SnapshotIdentifier snapshotIdentifier = SnapshotIdentifier.ofEntry(this, element);
			snapshotIdentifier.appendVersion(metadata);
		});
		return metadata;
	}

	private String completeVersion() {
		return versionQualifier.isPresent() ? (versionBare + QUALIFIER_SEPARATOR + versionQualifier.get()) : versionBare;
	}

	@Override
	public Path asBasePath() {
		Path artifactPath = moduleIdentifier.asBasePath();
		String version = completeVersion();
		return artifactPath.resolve(version);
	}

	public String asFileBaseName() {
		return moduleIdentifier.asFileBaseName(versionBare);
	}


	public Path asUploadPath(ArtifactIdentifier.BuildGenerator buildGenerator,
							 Optional<String> classifier, String packaging) {
		PackageIdentifier packageIdentifier = PackageIdentifier.of(this, classifier, packaging);
		if(isSnapshot()) {
			return buildGenerator.generate(packageIdentifier).asBuildPath();
		}
		return packageIdentifier.asArtifactPath();
	}

	public Path asFetchPath(EntryLister lister, Optional<String> classifier, String packaging) {
		if(isSnapshot()) {
			List<SnapshotIdentifier> candidates = new LinkedList<>();
			lister.list(element -> {
				SnapshotIdentifier candidate = SnapshotIdentifier.ofEntry(this, element);
				candidates.add(candidate);
			});
			if(candidates.isEmpty()) {
				return asArtifactPath(classifier, packaging);
			}
			SnapshotIdentifier snapshotIdentifier = SnapshotIdentifier.newest(candidates);
			return snapshotIdentifier.asFetchPath(lister);
		}
		return asArtifactPath(classifier, packaging);
	}

	public Path asArtifactPath(Optional<String> classifier, String packaging) {
		String fileName = moduleIdentifier.asFileName(completeVersion(), classifier, packaging);
		return asBasePath().resolve(fileName);
	}

	public Path asBuildPath(Optional<String> classifier, String packaging, LocalDateTime timestamp, int buildId) {
		String fileName = moduleIdentifier.asSnapshotFilename(versionBare, classifier, packaging, timestamp, buildId);
		return asBasePath().resolve(fileName);
	}

	private boolean isSnapshot() {
		return versionQualifier.isPresent() && versionQualifier.get().equals("SNAPSHOT");
	}

	public void addSnapshotVersion(Metadata metadata, Optional<String> classifier, String packaging, LocalDateTime timestamp, int buildId) {
		String version = versionBare + "-" + timestamp.format(TIMESTAMP_FORMATTER) + "-" + buildId;
		metadata.addSnapshotVersion(classifier.orElse(""), packaging, version, buildId, timestamp);
	}

	public void appendVersion(Metadata metadata) {
		String version = completeVersion();
		metadata.addVersion(version);
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesVersion(moduleIdentifier, versionBare, versionQualifier);
	}
}
