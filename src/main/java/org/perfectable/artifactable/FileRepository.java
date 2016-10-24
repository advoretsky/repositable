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
	public Optional<Metadata> findMetadata(MetadataIdentifier metadataIdentifier) {
		Metadata metadata = metadataIdentifier.createEmptyMetadata();
		Path artifactPath = metadataIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				MetadataIdentifier.Entry entry = metadataIdentifier.createEntry(versionPath);
				entry.appendVersion(metadata);
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
	public Optional<Artifact> findArtifact(ArtifactIdentifier releaseIdentifier) {
		Path artifactPath = releaseIdentifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(byteSource));
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact artifact) {
		Path artifactPath = identifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		Path parent = absolutePath.resolveSibling(".");
		createDirectoryIfNeeded(parent);
		try {
			InputStream source = artifact.openStream();
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

