package org.perfectable.repositable;

import java.io.OutputStream;

import com.google.common.net.MediaType;

public interface OutputStreamTransformer {
	MediaType transformMediaType(MediaType original);

	OutputStream transformStream(OutputStream raw);
}
