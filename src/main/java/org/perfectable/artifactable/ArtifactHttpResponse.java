package org.perfectable.artifactable;

import org.perfectable.webable.handler.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ArtifactHttpResponse implements HttpResponse {

	private final Artifact artifact;

	private ArtifactHttpResponse(Artifact artifact) {
		this.artifact = artifact;
	}

	public static HttpResponse of(Artifact artifact) {
		return new ArtifactHttpResponse(artifact);
	}

	@Override
	public void writeTo(HttpServletResponse resp) throws IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		artifact.writeContent(resp.getOutputStream());
	}
}
