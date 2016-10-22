package org.perfectable.artifactable;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;


public class MetadataHttpResponse implements HttpResponse {
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
	public void writeTo(HttpServletResponse resp) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(Metadata.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			try(OutputStream hashedStream = hashMethod.wrapOutputStream(resp.getOutputStream())) {
				marshaller.marshal(metadata, hashedStream);
			}
		}
		catch (JAXBException e) {
			throw new AssertionError(e);
		}
	}
}
