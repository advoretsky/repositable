package org.perfectable.repositable;

import com.google.common.io.ByteSource;
import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;

public interface ArtifactLocation {
	Optional<Artifact> find(Repositories repositories);

	HttpResponse transformResponse(HttpResponse response);

	void add(Repositories repositories, ByteSource source, User uploader)
			throws UnauthorizedUserException, InsertionRejected;
}
