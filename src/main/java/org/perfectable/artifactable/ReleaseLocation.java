package org.perfectable.artifactable;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class ReleaseLocation {

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.0/buildable-1.2.0.jar"
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([0-9][\\w\\.-]*?)\\/\\3-\\4(?:-([a-z])+)?\\.(\\w+)(?:\\.(\\w+))?$");

	final String repositoryName;
	final VersionIdentifier versionIdentifier;
	final HashMethod hashMethod;

	private ReleaseLocation(String repositoryName, VersionIdentifier versionIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.versionIdentifier = versionIdentifier;
		this.hashMethod = hashMethod;
	}

	public static boolean matchesPath(String path)
	{
		return PATH_PATTERN.matcher(path).matches();
	}

	public static ReleaseLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace('/', '.');
		String artifactId = matcher.group(3);
		String version = matcher.group(4);
		String classifier = matcher.group(6);
		String packaging = matcher.group(6);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(7));
		ArtifactIdentifier artifactIdentifier = ArtifactIdentifier.of(groupId, artifactId);
		VersionIdentifier versionIdentifier = VersionIdentifier.of(artifactIdentifier, version, null, classifier, packaging);
		return new ReleaseLocation(repositoryName, versionIdentifier, hashMethod);
	}
}
