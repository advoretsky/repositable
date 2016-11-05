package org.perfectable.repositable;

import org.perfectable.webable.handler.HttpRequest;

import java.io.InputStream;
import java.io.OutputStream;

public final class PublishedArtifact implements Artifact {
	private final HttpRequest request;

	private PublishedArtifact(HttpRequest request) {
		this.request = request;
	}

	public static PublishedArtifact of(HttpRequest request) {
		return new PublishedArtifact(request);
	}

	@Override
	public void writeContent(OutputStream stream) {
		throw new UnsupportedOperationException("Cannot write to published artifact");
	}

	@Override
	public InputStream openStream() {
		return request.contentStream();
	}
}
