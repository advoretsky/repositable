package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;

public final class HashedHttpResponse implements HttpResponse {

	private final Artifact artifact;
	private final HashMethod hashMethod;

	private HashedHttpResponse(Artifact artifact, HashMethod hashMethod) {
		this.artifact = artifact;
		this.hashMethod = hashMethod;
	}

	public static HttpResponse of(Artifact artifact, HashMethod hashMethod) {
		return new HashedHttpResponse(artifact, hashMethod);
	}

	@Override
	public void writeTo(Writer writer) throws IOException {
		writer.setStatus(HttpStatus.OK);
		try(OutputStream hashingStream = hashMethod.wrapOutputStream(writer.stream())) {
			artifact.writeContent(hashingStream);
		}
	}
}
