package org.perfectable.repositable.repository;

import com.google.common.io.Files;
import org.perfectable.repositable.Artifact;
import org.perfectable.repositable.ArtifactIdentifier;
import org.perfectable.repositable.FileArtifact;
import org.perfectable.repositable.HashMethod;
import org.perfectable.repositable.InsertionRejected;
import org.perfectable.repositable.MetadataIdentifier;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class FileRepository implements Repository {
	private final Path location;

	public FileRepository(Path location) {
		this.location = location;
	}

	public static FileRepository create(Path location) {
		return new FileRepository(location);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		return metadataIdentifier.createMetadata(location);
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier identifier) {
		Path artifactPath = identifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		return Optional.of(FileArtifact.of(absolutePath));
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact artifact, User uploader, HashMethod hashMethod)
			throws UnauthorizedUserException, InsertionRejected {
		if(hashMethod != HashMethod.NONE) {
			return; // dont put hashes into repository
		}
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

