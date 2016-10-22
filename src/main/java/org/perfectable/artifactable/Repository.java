package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.perfectable.artifactable.metadata.Metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.Files.newDirectoryStream;

@XmlAccessorType(XmlAccessType.NONE)
public class Repository {

	@XmlAttribute(name = "name")
	String name;

	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location")
	private Path location;

	public Optional<Metadata> findMetadata(ArtifactIdentifier artifactIdentifier) {
		Metadata metadata = new Metadata();
		metadata.setArtifactId(artifactIdentifier.artifactId);
		metadata.setGroupId(artifactIdentifier.groupId);
		Path artifactPath = artifactIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				VersionIdentifier versionIdentifier = VersionIdentifier.ofEntry(artifactIdentifier, versionPath);
				String version = versionIdentifier.completeVersion();
				if("SNAPSHOT".equals(versionIdentifier.versionModifier)) {
					// MARK ignore snapshots
				}
				else {
					metadata.addVersion(version);
				}
			}
		}
		catch (NoSuchFileException e) {
			// just dont add versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return Optional.of(metadata);
	}

	public Optional<Metadata> findMetadata(VersionIdentifier versionIdentifier) {
		Metadata metadata = new Metadata();
		metadata.setArtifactId(versionIdentifier.artifactIdentifier.artifactId);
		metadata.setGroupId(versionIdentifier.artifactIdentifier.groupId);
		Path artifactPath = versionIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionEntryStream = newDirectoryStream(absolutePath)) {
			for (Path versionEntryPath : versionEntryStream) {
				SnapshotIdentifier target = SnapshotIdentifier.ofEntry(versionIdentifier, versionEntryPath);
				String version = target.versionIdentifier.versionBare + "-" + target.timestamp.format(SnapshotIdentifier.TIMESTAMP_FORMATTER) + "-" + target.buildId;
				if("SNAPSHOT".equals(target.versionIdentifier.versionModifier)) {
					metadata.addSnapshotVersion(target.versionIdentifier.packaging, version, target.timestamp);
				}
				else {
					metadata.addVersion(version);
				}
			}
		}
		catch (NoSuchFileException e) {
			// just dont add versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return Optional.of(metadata);
	}

	public Optional<Artifact> findArtifact(SnapshotIdentifier snapshotIdentifier) {
		Path artifactPath = snapshotIdentifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(snapshotIdentifier, byteSource));
	}

	public Optional<Artifact> findArtifact(VersionIdentifier releaseIdentifier) {
		Path artifactPath = releaseIdentifier.asFilePath();
		Path absolutePath = location.resolve(artifactPath);
		if(!absolutePath.toFile().exists()) {
			return Optional.empty();
		}
		ByteSource byteSource = Files.asByteSource(absolutePath.toFile());
		return Optional.of(Artifact.of(releaseIdentifier, byteSource));
	}


	public void put(Artifact artifact) {
		Path artifactPath = artifact.asPath();
		Path absolutePath = location.resolve(artifactPath);
		Path parent = absolutePath.resolveSibling(".");
		createDirectoryIfNeeded(parent);
		try {
			InputStream source = artifact.byteSource.openStream();
			Files.asByteSink(absolutePath.toFile()).writeFrom(source);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private void createDirectoryIfNeeded(Path parent) {
		parent.toFile().mkdirs();
	}


}

