package org.perfectable.repositable.repository;

import org.perfectable.repositable.Artifact;
import org.perfectable.repositable.ArtifactIdentifier;
import org.perfectable.repositable.HashMethod;
import org.perfectable.repositable.InsertionRejected;
import org.perfectable.repositable.MetadataIdentifier;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.authorization.UserSet;
import org.perfectable.repositable.metadata.Metadata;

import java.util.Optional;

public final class AuthorizedRepository implements Repository {
	private final Repository wrapped;
	private final UserSet uploaders;

	private AuthorizedRepository(Repository wrapped, UserSet uploaders) {
		this.wrapped = wrapped;
		this.uploaders = uploaders;
	}

	public static AuthorizedRepository of(Repository repository, UserSet uploaders) {
		return new AuthorizedRepository(repository, uploaders);
	}

	@Override
	public Metadata fetchMetadata(MetadataIdentifier metadataIdentifier) {
		return wrapped.fetchMetadata(metadataIdentifier);
	}

	@Override
	public Optional<Artifact> findArtifact(ArtifactIdentifier artifactIdentifier) {
		return wrapped.findArtifact(artifactIdentifier);
	}

	@Override
	public void put(ArtifactIdentifier identifier, Artifact content, User uploader, HashMethod hashMethod) throws UnauthorizedUserException, InsertionRejected {
		if(!uploaders.contains(uploader)) {
			throw new UnauthorizedUserException();
		}
		wrapped.put(identifier, content, uploader, hashMethod);
	}

	@Override
	public Repository restrictUploaders(UserSet uploaders) {
		return new AuthorizedRepository(wrapped, this.uploaders.intersection(uploaders));
	}
}
