package org.perfectable.repositable;

import org.perfectable.repositable.authorization.UnauthorizedUserException;
import org.perfectable.repositable.authorization.User;
import org.perfectable.webable.handler.HttpResponse;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class SnapshotLocation implements ArtifactLocation {
	private static final String REPRESENTATION_FORMAT = "SnapshotLocation(%s, %s, %s)";

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.1-SNAPSHOT/buildable-1.2.1-20161022.184306-1.jar" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([0-9][\\w\\.-]*?)-SNAPSHOT\\/\\3-\\4-([0-9]{8}\\.[0-9]{6})-([0-9]+)(?:-([a-z-]+))?\\.(\\w+)(?:\\.(\\w+))?$");

	private final String repositoryName;
	private final SnapshotIdentifier snapshotIdentifier;
	private final HashMethod hashMethod;

	private SnapshotLocation(String repositoryName, SnapshotIdentifier snapshotIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.snapshotIdentifier = snapshotIdentifier;
		this.hashMethod = hashMethod;
	}

	public static SnapshotLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace('/', '.');
		String artifactId = matcher.group(3);
		String versionBare = matcher.group(4);
		String timestampString = matcher.group(5);
		LocalDateTime timestamp = LocalDateTime.parse(timestampString, SnapshotIdentifier.TIMESTAMP_FORMATTER);
		int buildId = Integer.parseInt(matcher.group(6));
		String classifier = matcher.group(7);
		String packaging = matcher.group(8);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(9));
		ModuleIdentifier moduleIdentifier = ModuleIdentifier.of(groupId, artifactId);
		VersionIdentifier versionIdentifier = VersionIdentifier.of(moduleIdentifier, versionBare, Optional.of("SNAPSHOT"));
		PackageIdentifier packageIdentifier = PackageIdentifier.of(versionIdentifier, Optional.ofNullable(classifier), packaging);
		SnapshotIdentifier snapshotIdentifier = SnapshotIdentifier.of(packageIdentifier, timestamp, buildId);
		return new SnapshotLocation(repositoryName, snapshotIdentifier, hashMethod);
	}

	@Override
	public Optional<Artifact> find(RepositorySelector repositorySelector) {
		Repository repository = repositorySelector.select(repositoryName);
		return repository.findArtifact(snapshotIdentifier);
	}

	@Override
	public void add(RepositorySelector repositories, Artifact artifact, User uploader)
			throws UnauthorizedUserException, InsertionRejected {
		Repository repository = repositories.select(repositoryName);
		repository.put(snapshotIdentifier, artifact, uploader, hashMethod);
	}

	@Override
	public HttpResponse transformResponse(HttpResponse response) {
		return TransformedHttpResponse.of(response, hashMethod);
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, repositoryName, snapshotIdentifier.asFilePath(), hashMethod);
	}
}
