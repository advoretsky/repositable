package org.perfectable.repositable;

import org.perfectable.testable.files.Directory;
import org.perfectable.testable.files.FilesExtension;
import org.perfectable.testable.files.Temporary;
import org.perfectable.webable.PortHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(FilesExtension.class)
public abstract class AbstractServerTest {

	@Temporary
	protected Directory baseDirectory;

	private int port;
	private Server.Monitor monitor;

	@BeforeEach
	public final void createServer() throws Exception {
		port = PortHelper.determineAvailablePort(20000, 30000);
		Server server = createBaseConfiguration()
				.withPort(port);
		monitor = server.serve();
	}

	protected abstract Server createBaseConfiguration() throws Exception;

	@AfterEach
	public final void closeServer() {
		monitor.close();
	}


	protected final URL createUrl(String path) {
		try {
			return new URL("http://localhost:" + port + path);
		}
		catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}

	protected final void createFile(String path, byte[] content) {
		baseDirectory.createFile(path)
				.withContent(content);
	}


	protected final void assertFile(String path, byte[] expectedContent) {
		baseDirectory.assertFile(path)
			.hasBinaryContent(expectedContent);
	}

	protected final void assertNoFile(String path) {
		baseDirectory.assertNoFile(path);
	}

	protected static String calculateBase64(String raw) {
		byte[] rawBytes = raw.getBytes(StandardCharsets.UTF_8);
		byte[] encodedBytes = Base64.getEncoder().encode(rawBytes);
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}

}
