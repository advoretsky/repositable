package org.perfectable.repositable;

import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.junit.Test;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.perfectable.webable.ConnectionAssertions.assertConnectionTo;

public class VirtualRepositoryTest extends AbstractServerTest {

	@Override
	protected Server createBaseConfiguration() throws IOException {
		User uploader = User.create("test-uploader", "test-uploader-password");
		Group uploaders = Group.create().join(uploader);
		File repository1Base = folder.newFolder("test-1");
		Repository repository1 = FileRepository.create(repository1Base.toPath())
				.restrictUploaders(uploaders);
		File repository2Base = folder.newFolder("test-2");
		Repository repository2 = FileRepository.create(repository2Base.toPath())
				.restrictUploaders(uploaders);
		Repositories sources = Repositories.create()
				.withAdditional("1", repository1)
				.withAdditional("2", repository2);
		VirtualRepository virtualRepository = VirtualRepository.create(sources);
		Repositories repositories = Repositories.create()
				.withAdditional("virtual", virtualRepository);
		return Server.create()
				.withRepositories(repositories)
				.withLoggableUser(uploaders);
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
	public void testArtifactReleaseMissing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseMd5Missing() throws IOException {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleaseSha1Missing() throws IOException {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactReleasePresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactReleaseMd5PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}


	@Test
	public void testArtifactReleaseSha1PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactReleasePresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactReleaseMd5PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactReleaseSha1PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactReleasePresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent1);
	}

	@Test
	public void testArtifactReleaseMd5PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent1).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactReleaseSha1PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent1).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotMissing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotMd5Missing() throws IOException {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotSha1Missing() throws IOException {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.isNotFound();
	}

	@Test
	public void testArtifactSnapshotPresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactSnapshotMd5PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotSha1PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotPresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent);
	}

	@Test
	public void testArtifactSnapshotMd5PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotSha1PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotPresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.hasContentType(MediaType.create("application", "x-java-archive"))
				.hasContent(artifactContent1);
	}

	@Test
	public void testArtifactSnapshotMd5PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashBytes(artifactContent1).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testArtifactSnapshotSha1PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashBytes(artifactContent1).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMissing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.isNotFound();
	}

	private static final String METADATA_RELASE_SINGLE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.test</groupId>\n" +
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
	public void testMetadataReleasePresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_RELASE_SINGLE);
	}

	@Test
	public void testMetadataReleasePresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_RELASE_SINGLE);
	}

	@Test
	public void testMetadataReleasePresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_RELASE_SINGLE);
	}

	private static final String METADATA_RELASE_DOUBLE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.test</groupId>\n" +
					"    <artifactId>test-artifact</artifactId>\n" +
					"    <versioning>\n" +
					"        <latest>1.2.2</latest>\n" +
					"        <versions>\n" +
					"            <version>1.2.2</version>\n" +
					"            <version>1.2.1</version>\n" +
					"        </versions>\n" +
					"        <snapshotVersions/>\n" +
					"    </versioning>\n" +
					"</metadata>\n";

	@Test
	public void testMetadataReleasePresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.2/test-artifact-1.2.2.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_RELASE_DOUBLE);
	}

	@Test
	public void testMetadataReleaseMd5Missing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseMd5PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMd5PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMd5PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseMd5PresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.2/test-artifact-1.2.2.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashString(METADATA_RELASE_DOUBLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseSha1Missing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataReleaseSha1PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseSha1PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseSha1PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataReleaseSha1PresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1/test-artifact-1.2.1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.2/test-artifact-1.2.2.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashString(METADATA_RELASE_DOUBLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotMissing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml"))
				.isNotFound();
	}

	private static final String METADATA_SNAPSHOT_SINGLE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.test</groupId>\n" +
					"    <artifactId>test-artifact</artifactId>\n" +
					"    <version>1.2.1-SNAPSHOT</version>\n" +
					"    <versioning>\n" +
					"        <snapshot>\n" +
					"            <timestamp>20161010.101010</timestamp>\n" +
					"            <buildNumber>1</buildNumber>\n" +
					"        </snapshot>\n" +
					"        <versions/>\n" +
					"        <lastUpdated>20161010101010</lastUpdated>\n" +
					"        <snapshotVersions>\n" +
					"            <snapshotVersion>\n" +
					"                <classifier></classifier>\n" +
					"                <extension>jar</extension>\n" +
					"                <value>1.2.1-20161010.101010-1</value>\n" +
					"                <updated>20161010101010</updated>\n" +
					"            </snapshotVersion>\n" +
					"        </snapshotVersions>\n" +
					"    </versioning>\n" +
					"</metadata>\n";

	@Test
	public void testMetadataSnapshotPresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_SNAPSHOT_SINGLE);
	}

	@Test
	public void testMetadataSnapshotPresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_SNAPSHOT_SINGLE);
	}

	@Test
	public void testMetadataSnapshotPresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_SNAPSHOT_SINGLE);
	}

	private static final String METADATA_SNAPSHOT_DOUBLE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<metadata>\n" +
					"    <groupId>org.perfectable.test</groupId>\n" +
					"    <artifactId>test-artifact</artifactId>\n" +
					"    <version>1.2.1-SNAPSHOT</version>\n" +
					"    <versioning>\n" +
					"        <snapshot>\n" +
					"            <timestamp>20161010.102020</timestamp>\n" +
					"            <buildNumber>2</buildNumber>\n" +
					"        </snapshot>\n" +
					"        <versions/>\n" +
					"        <lastUpdated>20161010102020</lastUpdated>\n" +
					"        <snapshotVersions>\n" +
					"            <snapshotVersion>\n" +
					"                <classifier></classifier>\n" +
					"                <extension>jar</extension>\n" +
					"                <value>1.2.1-20161010.102020-2</value>\n" +
					"                <updated>20161010102020</updated>\n" +
					"            </snapshotVersion>\n" +
					"            <snapshotVersion>\n" +
					"                <classifier></classifier>\n" +
					"                <extension>jar</extension>\n" +
					"                <value>1.2.1-20161010.101010-1</value>\n" +
					"                <updated>20161010101010</updated>\n" +
					"            </snapshotVersion>\n" +
					"        </snapshotVersions>\n" +
					"    </versioning>\n" +
					"</metadata>\n";

	@Test
	public void testMetadataSnapshotPresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.102020-2.jar", artifactContent2);
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.XML_UTF_8)
				.hasContentXml(METADATA_SNAPSHOT_DOUBLE);
	}

	@Test
	public void testMetadataSnapshotMd5Missing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.md5"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotMd5PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotMd5PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotMd5PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotMd5PresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.102020-2.jar", artifactContent2);
		String calculatedHash = Hashing.md5().hashString(METADATA_SNAPSHOT_DOUBLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.md5"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotSha1Missing() {
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.isNotFound();
	}

	@Test
	public void testMetadataSnapshotSha1PresentIn1() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotSha1PresentIn2() throws IOException {
		byte[] artifactContent = {2,5,2,100};
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotSha1PresentInBoth() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT_SINGLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testMetadataSnapshotSha1PresentInBothDifferent() throws IOException {
		byte[] artifactContent1 = {2,5,2,100};
		byte[] artifactContent2 = {2,5,2,120};
		createFile("test-1/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.101010-1.jar", artifactContent1);
		createFile("test-2/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/test-artifact-1.2.1-20161010.102020-2.jar", artifactContent2);
		String calculatedHash = Hashing.sha1().hashString(METADATA_SNAPSHOT_DOUBLE, StandardCharsets.UTF_8).toString();
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.2.1-SNAPSHOT/maven-metadata.xml.sha1"))
				.returnedStatus(HttpServletResponse.SC_OK)
				.hasContentType(MediaType.create("text", "plain"))
				.hasContentText(calculatedHash);
	}

	@Test
	public void testPutReleaseInvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseInvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseValid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutReleaseSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.0/test-artifact-1.0.0.jar");
	}

	@Test
	public void testPutSnapshotInvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotInvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotValid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotMd5InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotMd5InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotMd5Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.md5"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidUser() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("missing-user:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1InvalidPassword() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:invalid-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

	@Test
	public void testPutSnapshotSha1Valid() {
		byte[] uploadedContent = {1, 2, 3};
		assertConnectionTo(createUrl("/virtual/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar.sha1"))
				.withMethod("PUT")
				.withHeader(HttpHeaders.AUTHORIZATION, "Basic " + calculateBase64("test-uploader:test-uploader-password"))
				.withContent(uploadedContent)
				.returnedStatus(HttpServletResponse.SC_FORBIDDEN);
		assertNoFile("test-1/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
		assertNoFile("test-2/org/perfectable/test/test-artifact/1.0.1-SNAPSHOT/test-artifact-1.0.1-20161001.101010-1.jar");
	}

}
