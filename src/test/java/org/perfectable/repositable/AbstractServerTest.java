package org.perfectable.repositable;

import com.google.common.base.Splitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.perfectable.webable.PortHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractServerTest {
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	private int port;
	private Server.Monitor monitor;

	@Before
	public final void createServer() throws Exception {
		port = PortHelper.determineAvailablePort(20000, 30000);
		Server server = createBaseConfiguration()
				.withPort(port);
		monitor = server.serve();
	}

	protected abstract Server createBaseConfiguration() throws Exception;

	@After
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

	protected final void createFile(String path, byte[] content) throws IOException {
		List<String> pathParts = Splitter.on("/").splitToList(path);
		List<String> directoryPath = pathParts.subList(0, pathParts.size() - 1);
		String fileName = pathParts.get(pathParts.size() -1);
		File baseDirectory = folder.newFolder(directoryPath.toArray(new String[directoryPath.size()]));
		File file = Paths.get(baseDirectory.getAbsolutePath(), fileName).toFile();
		try(OutputStream stream = new FileOutputStream(file)) {
			stream.write(content);
		}
	}

}
