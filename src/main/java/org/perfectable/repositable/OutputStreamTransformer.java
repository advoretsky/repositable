package org.perfectable.repositable;

import java.io.OutputStream;

@FunctionalInterface
public interface OutputStreamTransformer {
	OutputStream transform(OutputStream raw);
}
