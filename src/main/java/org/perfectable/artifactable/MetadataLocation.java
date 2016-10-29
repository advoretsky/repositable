package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;

public interface MetadataLocation {
	Optional<Metadata> find(Repositories repositories);

	HttpResponse createResponse(Metadata metadata);
}
