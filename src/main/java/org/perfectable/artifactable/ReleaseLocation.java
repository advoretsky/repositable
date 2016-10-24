package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class ReleaseLocation {

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.0/buildable-1.2.0.jar"
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([0-9][\\w\\.-]*?)\\/\\3-\\4(?:-([a-z])+)?\\.(\\w+)(?:\\.(\\w+))?$");

	private final String repositoryName;
	private final VersionIdentifier versionIdentifier;
	private final HashMethod hashMethod;

	private ReleaseLocation(String repositoryName, VersionIdentifier versionIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.versionIdentifier = versionIdentifier;
		this.hashMethod = hashMethod;
	}

	public static ReleaseLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace('/', '.');
		String artifactId = matcher.group(3);
		String version = matcher.group(4);
		String classifier = matcher.group(5);
		String packaging = matcher.group(6);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(7));
		ModuleIdentifier moduleIdentifier = ModuleIdentifier.of(groupId, artifactId);
		VersionIdentifier versionIdentifier = VersionIdentifier.of(moduleIdentifier, version, Optional.empty(), Optional.ofNullable(classifier), packaging);
		return new ReleaseLocation(repositoryName, versionIdentifier, hashMethod);
	}

	public Optional<Artifact> find(Repositories repositories) {
		return repositories.findArtifact(repositoryName, versionIdentifier);
	}

	public void add(Repositories repositories, ByteSource source) {
		if(hashMethod != HashMethod.NONE) {
			return;
		}
		repositories.add(repositoryName, versionIdentifier, source);
	}

	public HttpResponse createResponse(Artifact artifact) {
		return HashedHttpResponse.of(artifact, hashMethod);
	}
}
