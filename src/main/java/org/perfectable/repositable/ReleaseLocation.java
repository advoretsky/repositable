package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class ReleaseLocation implements ArtifactLocation {

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.0/buildable-1.2.0.jar"
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([0-9][\\w\\.-]*?)\\/\\3-\\4(?:-([a-z-]+))?\\.(\\w+)(?:\\.(\\w+))?$");

	private static final String REPRESENTATION_FORMAT = "ReleaseLocation(%s, %s, %s)";

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

	@Override
	public Optional<Artifact> find(RepositorySelector repositorySelector) {
		Repository repository = repositorySelector.select(repositoryName);
		return repository.findArtifact(versionIdentifier);
	}

	@Override
	public void add(RepositorySelector repositorySelector, Artifact artifact, User uploader)
			throws UnauthorizedUserException, InsertionRejected {
		if(hashMethod != HashMethod.NONE) {
			return;
		}
		Repository repository = repositorySelector.select(repositoryName);
		repository.put(versionIdentifier, artifact, uploader);
	}

	@Override
	public HttpResponse transformResponse(HttpResponse response) {
		return TransformedHttpResponse.of(response, hashMethod);
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, repositoryName, versionIdentifier.asFilePath(), hashMethod);
	}
}
