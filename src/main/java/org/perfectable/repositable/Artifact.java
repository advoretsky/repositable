package org.perfectable.repositable;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.net.MediaType;

public interface Artifact {
	void writeContent(OutputStream stream);

	InputStream openStream();

	MediaType mediaType();
}
