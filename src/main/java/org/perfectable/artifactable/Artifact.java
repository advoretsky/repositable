package org.perfectable.artifactable;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Artifact {
	private final ByteSource byteSource;

	public static Artifact of(ByteSource byteSource) {
		return new Artifact(byteSource);
	}

	public Artifact(ByteSource byteSource) {
		this.byteSource = byteSource;
	}

	public void writeContent(OutputStream outputStream) {
		try {
			byteSource.copyTo(outputStream);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public InputStream openStream() {
		try {
			return byteSource.openStream();
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
