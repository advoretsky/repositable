package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.file.Files.newDirectoryStream;

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
	public Metadata createMetadata(Path location) {
		Metadata metadata = createEmptyMetadata();
		Path artifactPath = asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				VersionIdentifier versionIdentifier = VersionIdentifier.ofEntry(this, versionPath);
				versionIdentifier.appendVersion(metadata);
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

	public boolean hasGroupId(String groupId) {
		return this.groupId.equals(groupId);
	}
}
