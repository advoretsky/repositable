package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.perfectable.artifactable.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.Files.newDirectoryStream;

public class FileRepository implements Repository {
	private final Path location;

	public FileRepository(Path location) {
		this.location = location;
	}

	public static FileRepository create(Path location) {
		return new FileRepository(location);
	}

	@Override
	public Optional<Metadata> findMetadata(ArtifactIdentifier artifactIdentifier) {
		Metadata metadata = artifactIdentifier.createEmptyMetadata();
		Path artifactPath = artifactIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				VersionIdentifier versionIdentifier = VersionIdentifier.ofEntry(artifactIdentifier, versionPath);
				String version = versionIdentifier.completeVersion();
				metadata.addVersion(version);
			}
		}
		catch (NoSuchFileException e) { // NOPMD
			// just dont addSnapshot versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return Optional.of(metadata);
	}

	@Override
	public Optional<Metadata> findMetadata(VersionIdentifier versionIdentifier) {
		Metadata metadata = versionIdentifier.createEmptyMetadata();
		Path artifactPath = versionIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionEntryStream = newDirectoryStream(absolutePath)) {
			for (Path versionEntryPath : versionEntryStream) {
				SnapshotIdentifier target = SnapshotIdentifier.ofEntry(versionIdentifier, versionEntryPath);
				target.appendVersion(metadata);
			}
		}
		catch (NoSuchFileException e) { // NOPMD
			// just dont addSnapshot versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return Optional.of(metadata);
	}

	@Override
	public Optional<Artifact> findArtifact(SnapshotIdentifier snapshotIdentifier) {
		Path artifactPath = snapshotIdentifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(snapshotIdentifier, byteSource));
	}


	@Override
	public Optional<Artifact> findArtifact(VersionIdentifier releaseIdentifier) {
		Path artifactPath = releaseIdentifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(releaseIdentifier, byteSource));
	}

	@Override
	public void put(Artifact artifact) {
		Path artifactPath = artifact.asPath();
		Path absolutePath = location.resolve(artifactPath);
		Path parent = absolutePath.resolveSibling(".");
		createDirectoryIfNeeded(parent);
		InputStream source = artifact.openStream();
		try {
			Files.asByteSink(absolutePath.toFile()).writeFrom(source);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private void createDirectoryIfNeeded(Path parent) {
		parent.toFile().mkdirs();
	}
}

