package org.perfectable.repositable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

public final class FileArtifact implements Artifact {
	private final Path sourceFile;

	public static FileArtifact of(Path sourceFile) {
		return new FileArtifact(sourceFile);
	}

	private FileArtifact(Path sourceFile) {
		this.sourceFile = sourceFile;
	}

	@Override
	public void writeContent(OutputStream outputStream) {
		try(FileInputStream inputStream = new FileInputStream(sourceFile.toFile())) {
			ByteStreams.copy(inputStream, outputStream);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public InputStream openStream() {
		try {
			return new FileInputStream(sourceFile.toFile());
		}
		catch (FileNotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public MediaType mediaType() {
		String mediaTypeString;
		try {
			mediaTypeString = Files.probeContentType(sourceFile);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		if(mediaTypeString == null) {
			return MediaType.OCTET_STREAM;
		}
		return MediaType.parse(mediaTypeString);
	}
}
