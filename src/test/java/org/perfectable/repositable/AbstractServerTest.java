package org.perfectable.repositable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.perfectable.webable.PortHelper;

import java.net.MalformedURLException;
import java.net.URL;

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

}
