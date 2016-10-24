package org.perfectable.artifactable;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class Artifact {
	private final FileIdentifier fileIdentifier;
	private final ByteSource byteSource;

	public static Artifact of(FileIdentifier fileIdentifier, ByteSource byteSource) {
		return new Artifact(fileIdentifier, byteSource);
	}

	public Artifact(FileIdentifier fileIdentifier, ByteSource byteSource) {
		this.fileIdentifier = fileIdentifier;
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

	public Path asPath() {
		return fileIdentifier.asFilePath();
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
