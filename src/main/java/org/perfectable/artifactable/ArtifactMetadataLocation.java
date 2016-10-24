package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class ArtifactMetadataLocation {

	// ex. "/libs-snapshot-local/org/perfectable/webable/1.1.0-SNAPSHOT/maven-metadata.xml" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]*)\\/([a-zA-Z][\\w-]*)\\/maven-metadata\\.xml(?:\\.(\\w+))?");

	private final String repositoryName;
	private final ArtifactIdentifier artifactIdentifier;
	private final HashMethod hashMethod;

	public ArtifactMetadataLocation(String repositoryName, ArtifactIdentifier artifactIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.artifactIdentifier = artifactIdentifier;
		this.hashMethod = hashMethod;
	}

	public static ArtifactMetadataLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace("/", ".");
		String artifactId = matcher.group(3);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(4));
		ArtifactIdentifier artifactIdentifier = ArtifactIdentifier.of(groupId, artifactId);
		return new ArtifactMetadataLocation(repositoryName, artifactIdentifier, hashMethod);
	}

	public Optional<Metadata> find(Repositories repositories) {
		return repositories.findMetadata(repositoryName, artifactIdentifier);
	}

	public MetadataHttpResponse createResponse(Metadata metadata) {
		return MetadataHttpResponse.of(metadata, hashMethod);
	}
}
