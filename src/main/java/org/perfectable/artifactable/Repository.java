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
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.newDirectoryStream;

@XmlAccessorType(XmlAccessType.NONE)
public class Repository {

	@XmlAttribute(name = "name")
	private String name;

	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location")
	private Path location;

	public Optional<Metadata> findMetadata(ArtifactIdentifier artifactIdentifier) {
		Metadata metadata = artifactIdentifier.createEmptyMetadata();
		Path artifactPath = artifactIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionStream = newDirectoryStream(absolutePath)) {
			for (Path versionPath : versionStream) {
				VersionIdentifier versionIdentifier = VersionIdentifier.ofEntry(artifactIdentifier, versionPath);
				String version = versionIdentifier.completeVersion();
				metadata.addVersion(version);
			}
		}
		catch (NoSuchFileException e) { // NOPMD
			// just dont add versions
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
		return Optional.of(metadata);
	}

	public Optional<Metadata> findMetadata(VersionIdentifier versionIdentifier) {
		Metadata metadata = versionIdentifier.createEmptyMetadata();
		Path artifactPath = versionIdentifier.asBasePath();
		Path absolutePath = location.resolve(artifactPath);
		try (DirectoryStream<Path> versionEntryStream = newDirectoryStream(absolutePath)) {
			for (Path versionEntryPath : versionEntryStream) {
				SnapshotIdentifier target = SnapshotIdentifier.ofEntry(versionIdentifier, versionEntryPath);
				target.appendVersion(metadata);
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
		InputStream source = artifact.openStream();
		try {
			Files.asByteSink(absolutePath.toFile()).writeFrom(source);
		}
		catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	private void createDirectoryIfNeeded(Path parent) {
		parent.toFile().mkdirs();
	}


	public static Optional<Repository> selectByName(List<Repository> repositories, String repositoryName) {
		return repositories.stream()
				.filter(r -> repositoryName.equals(r.name))
				.findFirst();
	}
}

