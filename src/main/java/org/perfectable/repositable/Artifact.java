package org.perfectable.repositable;

import com.google.common.net.MediaType;

import java.io.InputStream;
import java.io.OutputStream;

public interface Artifact {
	void writeContent(OutputStream stream);

	InputStream openStream();

	MediaType mediaType();
}
