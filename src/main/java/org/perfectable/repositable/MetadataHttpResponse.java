package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;


public final class MetadataHttpResponse implements HttpResponse {
	private final Metadata metadata;
	private final HashMethod hashMethod;

	private MetadataHttpResponse(Metadata metadata, HashMethod hashMethod) {
		this.metadata = metadata;
		this.hashMethod = hashMethod;
	}

	public static MetadataHttpResponse of(Metadata metadata, HashMethod hashMethod) {
		return new MetadataHttpResponse(metadata, hashMethod);
	}

	@Override
	public void writeTo(Writer writer) throws IOException {
		try(OutputStream hashedStream = hashMethod.wrapOutputStream(writer.stream())) {
			metadata.writeInto(hashedStream);
		}
	}
}
