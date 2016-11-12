package org.perfectable.repositable;

import org.perfectable.webable.handler.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

public interface Artifact {
	InputStream openStream() throws IOException;

	HttpResponse asResponse();
}
