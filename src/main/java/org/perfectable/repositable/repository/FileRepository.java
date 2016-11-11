package org.perfectable.repositable.repository;

import org.perfectable.repositable.Artifact;
import org.perfectable.repositable.ArtifactIdentifier;
import org.perfectable.repositable.EntryLister;
import org.perfectable.repositable.FileArtifact;
import org.perfectable.repositable.HashMethod;
import org.perfectable.repositable.InsertionRejected;
import org.perfectable.repositable.MetadataIdentifier;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.SnapshotIdentifier;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import com.google.common.io.Files;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.file.Files.newDirectoryStream;

public final class FileRepository implements Repository {
	private static final ArtifactIdentifier.BuildGenerator DEFAULT_BUILD_GENERATOR =
			packageIdentifier -> SnapshotIdentifier.of(packageIdentifier, LocalDateTime.now(ZoneOffset.UTC), 1);

	private final Path location;

	private final ArtifactIdentifier.BuildGenerator buildGenerator;

	private FileRepository(Path location, ArtifactIdentifier.BuildGenerator buildGenerator) {
		this.location = location;
		this.buildGenerator = buildGenerator;
	}

	public static FileRepository create(Path location) {
		return new FileRepository(location, DEFAULT_BUILD_GENERATOR);
	}

	public FileRepository withBuildGenerator(ArtifactIdentifier.BuildGenerator newBuildGenerator) {
		return new FileRepository(location, newBuildGenerator);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		Path absolutePath = location.resolve(metadataIdentifier.asBasePath());
		EntryLister lister = new DirectoryLister(absolutePath);
		return metadataIdentifier.createMetadata(lister);
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier identifier) {
		Path absolutePath = location.resolve(identifier.asBasePath());
		EntryLister lister = new DirectoryLister(absolutePath);
		Path artifactPath = location.resolve(identifier.asFetchPath(lister));
		if (!artifactPath.toFile().exists()) {
			return Optional.empty();
		}
		return Optional.of(FileArtifact.of(artifactPath));
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact artifact, User uploader, HashMethod hashMethod)
			throws UnauthorizedUserException, InsertionRejected {
		if (hashMethod != HashMethod.NONE) {
			return; // dont put hashes into repository
		}
		Path artifactPath = identifier.asUploadPath(buildGenerator);
		Path absolutePath = location.resolve(artifactPath);
		Path parent = checkNotNull(absolutePath.getParent());
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

	private static class DirectoryLister implements EntryLister {
		private final Path basePath;

		DirectoryLister(Path basePath) {
			this.basePath = basePath;
		}

		@Override
		public void list(Consumer consumer) {
			try (DirectoryStream<Path> stream = newDirectoryStream(basePath)) {
				for (Path elementPath : stream) {
					if (elementPath == null) {
						continue;
					}
					Path fileName = elementPath.getFileName();
					if (fileName == null) {
						continue;
					}
					consumer.entry(fileName.toString());
				}
			}
			catch (NoSuchFileException e) { // NOPMD
				// no entries
			}
			catch (IOException e) {
				throw new AssertionError(e);
			}

		}
	}
}

