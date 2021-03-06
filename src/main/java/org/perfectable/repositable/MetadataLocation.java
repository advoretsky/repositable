package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

public interface MetadataLocation {
	Metadata fetch(RepositorySelector repositorySelector);

	HttpResponse transformResponse(HttpResponse response);
}
