package org.perfectable.repositable;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

public class FileRepository implements Repository {
	private final Path location;
	private final Filter filter;

	public FileRepository(Path location, Filter filter) {
		this.location = location;
		this.filter = filter;
	}

	public static FileRepository create(Path location, Filter filter) {
		return new FileRepository(location, filter);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		return metadataIdentifier.createMetadata(location);
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier identifier) {
		if(!identifier.matches(filter)) {
			return Optional.empty();
		}
		Path artifactPath = identifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(byteSource));
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact artifact, User uploader) throws UnauthorizedUserException, InsertionRejected {
		if(!identifier.matches(filter)) {
			throw new InsertionRejected();
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

