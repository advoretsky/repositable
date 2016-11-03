package org.perfectable.repositable;

import com.google.common.net.MediaType;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;

public final class TransformedHttpResponse implements HttpResponse {
	private final HttpResponse wrapped;
	private final OutputStreamTransformer transformer;

	private TransformedHttpResponse(HttpResponse wrapped, OutputStreamTransformer transformer) {
		this.wrapped = wrapped;
		this.transformer = transformer;
	}

	public static TransformedHttpResponse of(HttpResponse wrapped, OutputStreamTransformer transformer) {
		return new TransformedHttpResponse(wrapped, transformer);
	}

	@Override
	public void writeTo(Writer writer) throws IOException {
		wrapped.writeTo(new WrappedWriter(writer));
	}

	private class WrappedWriter implements Writer {
		private final Writer wrapped;

		WrappedWriter(Writer wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void setStatus(HttpStatus httpStatus) {
			wrapped.setStatus(httpStatus);
		}

		@Override
		public void setContentType(MediaType mediaType) {
			wrapped.setContentType(mediaType);
		}

		@Override
		public OutputStream stream() throws IOException {
			return transformer.transform(wrapped.stream());
		}
	}
}
