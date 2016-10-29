package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.artifactable.authorization.UnauthorizedUserException;
import org.perfectable.artifactable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;

public interface ArtifactLocation {
	Optional<Artifact> find(Repositories repositories);

	HttpResponse createResponse(Artifact artifact);

	void add(Repositories repositories, ByteSource source, User uploader)
			throws UnauthorizedUserException;
}
