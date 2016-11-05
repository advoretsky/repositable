package org.perfectable.repositable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.perfectable.webable.PortHelper;

import java.net.MalformedURLException;
import java.net.URL;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class EmptyTest {
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	private int port;
	private Server.Monitor monitor;

	@Before
	public void createServer() {
		port = PortHelper.determineAvailablePort(20000, 30000);
		Server server = Server.create()
				.withPort(port);
		monitor = server.serve();
	}

	@After
	public void closeServer() {
		monitor.close();
	}

	@Test
	public void testRoot() {
		assertConnectionTo(createUrl("/"))
				.isNotFound();
	}

	@Test
	public void testArtifactRelease() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	private URL createUrl(String path) {
		try {
			return new URL("http://localhost:" + port + path);
		}
		catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}
}
