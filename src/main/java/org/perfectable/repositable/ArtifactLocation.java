package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;

public interface ArtifactLocation {
	Optional<Artifact> find(RepositorySelector repositorySelector);

	HttpResponse transformResponse(HttpResponse response);

	void add(RepositorySelector repositories, Artifact source, User uploader)
			throws UnauthorizedUserException, InsertionRejected;
}
