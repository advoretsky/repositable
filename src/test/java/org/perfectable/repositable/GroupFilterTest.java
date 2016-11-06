package org.perfectable.repositable;

import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;
import org.junit.Test;
import org.perfectable.repositable.filter.GroupFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class GroupFilterTest extends AbstractServerTest {

	@Override
	protected Server createBaseConfiguration() throws IOException {
		File repositoryBase = folder.newFolder("test-content");
		Repository repository =
				FileRepository.create(repositoryBase.toPath())
					.filtered(GroupFilter.of("org.perfectable.allowed"));
		Repositories repositories = Repositories.create()
				.withAdditional("test-repository", repository);
		return Server.create()
				.withRepositories(repositories);
	}

	@Test
	public void testArtifactReleaseMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleasePresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleasePresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactReleaseMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5PresenMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactReleaseSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1PresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotPresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotPresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
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
	public void testArtifactSnapshotMd5PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotMd5PresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
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
	public void testArtifactSnapshotSha1PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotSha1PresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	private static final String METADATA_RELASE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.allowed</groupId>\n" +
					"    <artifactId>test-artifact</artifactId>\n" +
					"    <versioning>\n" +
					"        <latest>1.2.1</latest>\n" +
					"        <versions>\n" +
					"            <version>1.2.1</version>\n" +
					"        </versions>\n" +
					"        <snapshotVersions/>\n" +
					"    </versioning>\n" +
					"</metadata>\n";

	@Test
	public void testMetadataReleasePresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_RELASE);
	}

	@Test
	public void testMetadataReleaseMd5Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentMd5NotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentMd5Matching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseSha1Missing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentSha1NotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleasePresentSha1Matching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotMissing() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	private static final String METADATA_SNAPSHOT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.allowed</groupId>\n" +
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
	public void testMetadataSnapshotPresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotPresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
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
	public void testMetadataSnapshotMd5PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotMd5PresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.md5"))
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
	public void testMetadataSnapshotSha1PresentNotMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotSha1PresentMatching() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-content/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/test-repository/org/perfectable/allowed/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testPutRelease() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutReleaseMd5() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutReleaseSha1() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshot() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshotMd5() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void testPutSnapshotSha1() {
		assertConnectionTo(createUrl("/test-repository/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withContent(new byte[] {1, 2, 3})
				.returnedStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
