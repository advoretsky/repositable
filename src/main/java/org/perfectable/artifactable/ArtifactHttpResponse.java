package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public final class ArtifactHttpResponse implements HttpResponse {

	private final Artifact artifact;
	private final HashMethod hashMethod;

	private ArtifactHttpResponse(Artifact artifact, HashMethod hashMethod) {
		this.artifact = artifact;
		this.hashMethod = hashMethod;
	}

	public static HttpResponse of(Artifact artifact, HashMethod hashMethod) {
		return new ArtifactHttpResponse(artifact, hashMethod);
	}

	@Override
	public void writeTo(HttpServletResponse resp) throws IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		try(OutputStream hashingStream = hashMethod.wrapOutputStream(resp.getOutputStream())) {
			artifact.writeContent(hashingStream);
		}
	}
}
