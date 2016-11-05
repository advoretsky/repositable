package org.perfectable.repositable;

import java.io.InputStream;
import java.io.OutputStream;

public interface Artifact {
	void writeContent(OutputStream stream);

	InputStream openStream();
}
