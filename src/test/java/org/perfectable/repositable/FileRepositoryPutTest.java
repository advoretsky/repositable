package org.perfectable.repositable;

import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import org.junit.Test;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class FileRepositoryPutTest extends AbstractServerTest {

	@Override
	protected Server createBaseConfiguration() throws IOException {
		File repositoryBase = folder.newFolder("test-content");
		User authorizedUser = User.create("test-user", "test-user-password");
		User uploader = User.create("test-uploader", "test-uploader-password");
		Group uploaders = Group.create()
				.join(uploader);
		Group loggableUsers = uploaders
				.join(authorizedUser);
		Repository repository = FileRepository.create(repositoryBase.toPath())
				.restrictUploaders(uploaders);
		Repositories repositories = Repositories.create()
				.withAdditional("test-repository", repository);
		return Server.create()
				.withRepositories(repositories)
				.withLoggableUser(loggableUsers);
	}

	// retrieval tests are at FileRepositoryTest

	@Test
	public void testPutReleaseInvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseInvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseUnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseValid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", uploadedContent);
	}

	@Test
	public void testPutReleaseMd5InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5UnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1UnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotInvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotInvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotUnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotValid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", uploadedContent);
	}

	@Test
	public void testPutSnapshotMd5InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotMd5InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotMd5UnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotSha1UnauthorizedUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-user:test-user-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	private static String calculateBase64(String raw) {
		byte[] rawBytes = raw.getBytes(StandardCharsets.UTF_8);
		byte[] encodedBytes = Base64.getEncoder().encode(rawBytes);
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}
}
