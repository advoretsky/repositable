package org.perfectable.artifactable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class ArtifactMetadataLocation {

	// ex. "/libs-snapshot-local/org/perfectable/webable/1.1.0-SNAPSHOT/maven-metadata.xml"
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]*)\\/([a-zA-Z][\\w-]*)\\/maven-metadata\\.xml(?:\\.(\\w+))?");

	final String repositoryName;
	final ArtifactIdentifier artifactIdentifier;
	final HashMethod hashMethod;

	public ArtifactMetadataLocation(String repositoryName, ArtifactIdentifier artifactIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.artifactIdentifier = artifactIdentifier;
		this.hashMethod = hashMethod;
	}

	public static boolean matchesPath(String path)
	{
		return PATH_PATTERN.matcher(path).matches();
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
}
