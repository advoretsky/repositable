package org.perfectable.repositable;

import org.perfectable.webable.handler.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileArtifact implements Artifact {
	private final Path sourceFile;

	public static FileArtifact of(Path sourceFile) {
		return new FileArtifact(sourceFile);
	}

	private FileArtifact(Path sourceFile) {
		this.sourceFile = sourceFile;
	}

	@Override
	public InputStream openStream() throws IOException {
		return Files.newInputStream(sourceFile, StandardOpenOption.READ);
	}

	@Override
	public HttpResponse asResponse() {
		return HttpResponse.OK.withGuessedFileContent(sourceFile);
	}
}
