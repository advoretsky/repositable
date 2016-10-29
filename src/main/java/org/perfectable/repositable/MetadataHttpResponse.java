package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
		try {
			JAXBContext context = JAXBContext.newInstance(Metadata.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			try(OutputStream hashedStream = hashMethod.wrapOutputStream(writer.stream())) {
				marshaller.marshal(metadata, hashedStream);
			}
		}
		catch (JAXBException e) {
			throw new AssertionError(e);
		}
	}
}
