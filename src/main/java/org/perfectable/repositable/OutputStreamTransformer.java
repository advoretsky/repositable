package org.perfectable.repositable;

import com.google.common.net.MediaType;

import java.io.OutputStream;

public interface OutputStreamTransformer {
	MediaType transformMediaType(MediaType original);

	OutputStream transformStream(OutputStream raw);
}
