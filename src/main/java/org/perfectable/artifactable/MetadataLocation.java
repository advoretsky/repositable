package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

public interface MetadataLocation {
	Metadata fetch(Repositories repositories);

	HttpResponse createResponse(Metadata metadata);
}
