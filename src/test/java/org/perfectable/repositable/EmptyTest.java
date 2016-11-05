package org.perfectable.repositable;

import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class EmptyTest extends AbstractServerTest {

	@Override
	protected Server createBaseConfiguration() {
		return Server.create();
	}

	@Test
	public void testRoot() {
		assertConnectionTo(createUrl("/"))
				.isNotFound();
	}

	@Test
	public void testArbitraryPath() {
		assertConnectionTo(createUrl("/test/arbitrary/path"))
				.isNotFound();
	}

	@Test
	public void testArtifactRelease() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshot() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataRelease() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshot() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testPutRelease() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutReleaseMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutReleaseSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshot() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshotMd5() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshotSha1() {
		assertConnectionTo(createUrl("/repository-name/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
