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


public final class VersionIdentifier implements ArtifactIdentifier, MetadataIdentifier {
	private final ModuleIdentifier moduleIdentifier;
	private final String versionBare;
	private final Optional<String> versionModifier;
	private final Optional<String> classifier;
	private final String packaging;

	public static VersionIdentifier of(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		return new VersionIdentifier(moduleIdentifier, versionBare, versionModifier, classifier, packaging);
	}

	private VersionIdentifier(ModuleIdentifier moduleIdentifier, String versionBare, Optional<String> versionModifier, Optional<String> classifier, String packaging) {
		this.moduleIdentifier = moduleIdentifier;
		this.versionBare = versionBare;
		this.classifier = classifier;
		this.versionModifier = versionModifier;
		this.packaging = packaging;
	}

	public VersionIdentifier withClassifier(String newClassifier) {
		return new VersionIdentifier(moduleIdentifier, versionBare, versionModifier, Optional.of(newClassifier), packaging);
	}

	public VersionIdentifier withPackaging(String newPackaging) {
		return new VersionIdentifier(moduleIdentifier, versionBare, versionModifier, classifier, newPackaging);
	}

	public Path asBasePath() {
		Path artifactPath = moduleIdentifier.asBasePath();
		String version = completeVersion();
		return artifactPath.resolve(version);
	}

	@Override
	public Path asFilePath() {
		Path versionPath = asBasePath();
		String version = completeVersion();
		String fileName = moduleIdentifier.asFileName(version, classifier, packaging);
		return versionPath.resolve(fileName);
	}

	public static VersionIdentifier ofEntry(ModuleIdentifier moduleIdentifier, Path versionPath) {
		Path entryPath = versionPath.subpath(1,versionPath.getNameCount());
		entryPath = moduleIdentifier.asBasePath().relativize(entryPath);
		Path filePath = checkNotNull(entryPath.getFileName());
		String fileName = filePath.toString();
		String versionBare;
		Optional<String> versionModifier;
		if(fileName.endsWith("-SNAPSHOT")) {
			versionBare = fileName.substring(0, fileName.length() - 9);
			versionModifier = Optional.of("SNAPSHOT");
		}
		else {
			versionBare = fileName;
			versionModifier = Optional.empty();
		}
		return of(moduleIdentifier, versionBare, versionModifier, Optional.empty(), "pom");
	}

	private String completeVersion() {
		return versionModifier.isPresent() ? (versionBare + "-" + versionModifier.get()) : versionBare;
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

	public Path asSnapshotPath(LocalDateTime timestamp, int buildId) {
		String timestampString = TIMESTAMP_FORMATTER.format(timestamp);
		Path artifactPath = asBasePath();
		String classifierSuffix = classifier.isPresent() ? "-" + classifier.get() : "";
		String fullVersion = versionBare + "-" + timestampString + "-" + buildId + classifierSuffix + "." + packaging;
		String fileName = moduleIdentifier.asSnapshotFilename(fullVersion);
		return artifactPath.resolve(fileName);

	}

	public void addSnapshotVersion(Metadata metadata, LocalDateTime timestamp, int buildId) {
		String version = versionBare + "-" + timestamp.format(TIMESTAMP_FORMATTER) + "-" + buildId;
		metadata.addSnapshotVersion(classifier.orElse(""), packaging, version, buildId, timestamp);
	}

	public void appendVersion(Metadata metadata) {
		String version = versionModifier.isPresent() ? (versionBare + "-" + versionModifier.get()) : versionBare;
		metadata.addVersion(version);
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesVersion(moduleIdentifier, versionBare, versionModifier, classifier, packaging);
	}

	public boolean hasGroupId(String groupId) {
		return moduleIdentifier.hasGroupId(groupId);
	}
}
