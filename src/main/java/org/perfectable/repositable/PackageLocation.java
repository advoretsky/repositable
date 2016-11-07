package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class PackageLocation implements ArtifactLocation {

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.0/buildable-1.2.0.jar"
	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.0-SNAPSHOT/buildable-1.2.0-SNAPSHOT.jar" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([\\d\\.]+)(?:-([\\w\\.-]+))?\\/\\3-\\4(?:-\\5)?(?:-([a-z-]+))?\\.(\\w+)(?:\\.(\\w+))?$");

	private static final String REPRESENTATION_FORMAT = "PackageLocation(%s, %s, %s)";

	private final String repositoryName;
	private final PackageIdentifier packageIdentifier;
	private final HashMethod hashMethod;

	private PackageLocation(String repositoryName, PackageIdentifier packageIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.packageIdentifier = packageIdentifier;
		this.hashMethod = hashMethod;
	}

	public static PackageLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace('/', '.');
		String artifactId = matcher.group(3);
		String versionBare = matcher.group(4);
		String versionQualifier = matcher.group(5);
		String classifier = matcher.group(6);
		String packaging = matcher.group(7);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(8));
		ModuleIdentifier moduleIdentifier = ModuleIdentifier.of(groupId, artifactId);
		VersionIdentifier versionIdentifier = VersionIdentifier.of(moduleIdentifier, versionBare, Optional.ofNullable(versionQualifier));
		PackageIdentifier packageIdentifier = PackageIdentifier.of(versionIdentifier, Optional.ofNullable(classifier), packaging);
		return new PackageLocation(repositoryName, packageIdentifier, hashMethod);
	}

	@Override
	public Optional<Artifact> find(RepositorySelector repositorySelector) {
		Repository repository = repositorySelector.select(repositoryName);
		return repository.findArtifact(packageIdentifier);
	}

	@Override
	public void add(RepositorySelector repositorySelector, Artifact artifact, User uploader)
			throws UnauthorizedUserException, InsertionRejected {
		Repository repository = repositorySelector.select(repositoryName);
		repository.put(packageIdentifier, artifact, uploader, hashMethod);
	}

	@Override
	public HttpResponse transformResponse(HttpResponse response) {
		return TransformedHttpResponse.of(response, hashMethod);
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, repositoryName, packageIdentifier.asUploadPath(), hashMethod);
	}
}
