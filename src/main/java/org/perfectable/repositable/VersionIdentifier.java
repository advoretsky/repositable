package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.Files.newDirectoryStream;
import static org.perfectable.repositable.SnapshotIdentifier.TIMESTAMP_FORMATTER;


public final class VersionIdentifier implements MetadataIdentifier {
	private static final char QUALIFIER_SEPARATOR = '-';
	private final ModuleIdentifier moduleIdentifier;
	private final String versionBare;
	private final Optional<String> versionQualifier;

	public static VersionIdentifier of(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier) {
		return new VersionIdentifier(moduleIdentifier, versionBare, versionQualifier);
	}

	private VersionIdentifier(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionQualifier) {
		this.moduleIdentifier = moduleIdentifier;
		this.versionBare = versionBare;
		this.versionQualifier = versionQualifier;
	}

	public Path asBasePath() {
		Path artifactPath = moduleIdentifier.asBasePath();
		String version = completeVersion();
		return artifactPath.resolve(version);
	}

	public static VersionIdentifier ofEntry(ModuleIdentifier moduleIdentifier, Path versionPath) {
		Path entryPath = versionPath.subpath(1,versionPath.getNameCount());
		entryPath = moduleIdentifier.asBasePath().relativize(entryPath);
		Path filePath = checkNotNull(entryPath.getFileName());
		String fileName = filePath.toString();
		String versionBare;
		Optional<String> versionQualifier;
		int qualifierStart = fileName.indexOf(QUALIFIER_SEPARATOR);
		if(qualifierStart >= 0) {
			versionBare = fileName.substring(0, qualifierStart);
			versionQualifier = Optional.of(fileName.substring(qualifierStart));
		}
		else {
			versionBare = fileName;
			versionQualifier = Optional.empty();
		}
		return of(moduleIdentifier, versionBare, versionQualifier);
	}

	private String completeVersion() {
		return versionQualifier.isPresent() ? (versionBare + QUALIFIER_SEPARATOR + versionQualifier.get()) : versionBare;
	}

	public String fileBaseName() {
		return moduleIdentifier.asFileBaseName(versionBare);
	}

	@Override
	public Metadata createEmptyMetadata() {
		Metadata metadata = moduleIdentifier.createEmptyMetadata();
		metadata.setVersion(completeVersion());
		return metadata;
	}

	@Override
	public Metadata createMetadata(Path location) {
		Metadata metadata = createEmptyMetadata();
		Path artifactPath = asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				SnapshotIdentifier snapshotIdentifier = SnapshotIdentifier.ofEntry(this, versionPath);
				snapshotIdentifier.appendVersion(metadata);
			}
		}
		catch (NoSuchFileException e) { // NOPMD
			// just dont addSnapshot versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return metadata;
	}

	public Path asPackagePath(Optional<String> classifier, String packaging) {
		Path versionPath = asBasePath();
		String version = completeVersion();
		String fileName = moduleIdentifier.asFileName(version, classifier, packaging);
		return versionPath.resolve(fileName);
	}

	public Path asSnapshotPath(Optional<String> classifier, String packaging, LocalDateTime timestamp, int buildId) {
		String timestampString = TIMESTAMP_FORMATTER.format(timestamp);
		Path artifactPath = asBasePath();
		String classifierSuffix = classifier.isPresent() ? QUALIFIER_SEPARATOR + classifier.get() : "";
		String fullVersion = versionBare + "-" + timestampString + "-" + buildId + classifierSuffix + "." + packaging;
		String fileName = moduleIdentifier.asSnapshotFilename(fullVersion);
		return artifactPath.resolve(fileName);
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
