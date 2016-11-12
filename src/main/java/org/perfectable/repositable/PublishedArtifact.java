package org.perfectable.repositable;

import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.net.MediaType;

public final class PublishedArtifact implements Artifact {
	private final HttpRequest request;

	private PublishedArtifact(HttpRequest request) {
		this.request = request;
	}

	public static PublishedArtifact of(HttpRequest request) {
		return new PublishedArtifact(request);
	}

	@Override
	public InputStream openStream() throws IOException {
		return request.contentStream();
	}

	@Override
	public HttpResponse asResponse() {
		MediaType contentType = request.declaredContentType()
				.orElse(MediaType.OCTET_STREAM);
		return HttpResponse.OK.withContentSource(contentType, request.contentSource());
	}
}
