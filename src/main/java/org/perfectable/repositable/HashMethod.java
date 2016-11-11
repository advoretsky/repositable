package org.perfectable.repositable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;

public enum HashMethod implements OutputStreamTransformer {
	NONE {
		@Override
		public MediaType transformMediaType(MediaType original) {
			return original;
		}

		@Override
		public OutputStream transformStream(OutputStream rawStream) {
			return rawStream;
		}
	},
	SHA1 {
		@Override
		public MediaType transformMediaType(MediaType original) {
			return HashingOutputStream.MEDIA_TYPE;
		}

		@Override
		public OutputStream transformStream(OutputStream rawStream) {
			Hasher hasher = Hashing.sha1().newHasher();
			return new HashingOutputStream(hasher, rawStream);
		}
	},
	MD5 {
		@Override
		public MediaType transformMediaType(MediaType original) {
			return HashingOutputStream.MEDIA_TYPE;
		}

		@Override
		public OutputStream transformStream(OutputStream rawStream) {
			Hasher hasher = Hashing.md5().newHasher();
			return new HashingOutputStream(hasher, rawStream);
		}
	};

	public static HashMethod byExtension(String extension) {
		if (extension == null) {
			return NONE;
		}
		switch (extension) {
			case "":
				return NONE;
			case "sha1":
				return SHA1;
			case "md5":
				return MD5;
			default:
				throw new AssertionError("Unknown hash method extension: " + extension);
		}
	}

	@Override
	public abstract OutputStream transformStream(OutputStream raw);

	private static class HashingOutputStream extends OutputStream {
		public static final MediaType MEDIA_TYPE = MediaType.create("text", "plain");
		private final Hasher hasher;
		private final OutputStream rawStream;

		HashingOutputStream(Hasher hasher, OutputStream rawStream) {
			this.hasher = hasher;
			this.rawStream = rawStream;
		}

		@Override
		public void write(int data) throws IOException {
			hasher.putByte((byte) data);
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			hasher.putBytes(bytes);
		}

		@Override
		public void write(byte[] bytes, int off, int len) throws IOException {
			hasher.putBytes(bytes, off, len);
		}

		@Override
		public void close() throws IOException {
			String hashText = hasher.hash().toString();
			byte[] hashBytes = hashText.getBytes(StandardCharsets.US_ASCII);
			rawStream.write(hashBytes);
			rawStream.flush();
			rawStream.close();
		}
	}
}
