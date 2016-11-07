package org.perfectable.repositable;

import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.junit.Test;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.filter.StabilityFilter;
import org.perfectable.repositable.repository.FileRepository;
import org.perfectable.repositable.repository.Repositories;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class SnapshotFilterTest extends AbstractServerTest {

	@Override
	protected Server createBaseConfiguration() throws IOException {
		File repositoryBase = folder.newFolder("test-content");
		User uploader = User.create("test-uploader", "test-uploader-password");
		Group uploaders = Group.create().join(uploader);
		Repository repository =
				FileRepository.create(repositoryBase.toPath())
					.filtered(StabilityFilter.SNAPSHOT)
					.restrictUploaders(uploaders);
		Repositories repositories = Repositories.create()
				.withAdditional("test-repository", repository);
		return Server.create()
				.withRepositories(repositories)
				.withLoggableUser(uploaders);
	}

	@Test
	public void testArtifactReleaseMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleasePresent() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotPresent() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactSnapshotMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotMd5Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotSha1Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresent() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentMd5() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseSha1Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	private static final String METADATA_SNAPSHOT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.test</groupId>\n" +
					"    <artifactId>test-artifact</artifactId>\n" +
					"    <version>1.0.1-SNAPSHOT</version>\n" +
					"    <versioning>\n" +
					"        <snapshot>\n" +
					"            <timestamp>20161001.101010</timestamp>\n" +
					"            <buildNumber>1</buildNumber>\n" +
					"        </snapshot>\n" +
					"        <versions/>\n" +
					"        <lastUpdated>20161001101010</lastUpdated>\n" +
					"        <snapshotVersions>\n" +
					"            <snapshotVersion>\n" +
					"                <classifier></classifier>\n" +
					"                <extension>jar</extension>\n" +
					"                <value>1.0.1-20161001.101010-1</value>\n" +
					"                <updated>20161001101010</updated>\n" +
					"            </snapshotVersion>\n" +
					"        </snapshotVersions>\n" +
					"    </versioning>\n" +
					"</metadata>\n";

	@Test
	public void testMetadataSnapshotPresent() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_SNAPSHOT);
	}

	@Test
	public void testMetadataSnapshotMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotMd5Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotSha1Present() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

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
	public void testPutReleaseValid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
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
	public void testPutReleaseMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
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
	public void testPutReleaseSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
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
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotMd5InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_OK);
		assertNoFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}


}
