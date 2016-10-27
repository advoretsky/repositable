package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.webable.handler.HttpResponse;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class SnapshotLocation {

	// ex. "/libs-snapshot-local/org/perfectable/buildable/1.2.1-SNAPSHOT/buildable-1.2.1-20161022.184306-1.jar" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]+)\\/([a-zA-Z][\\w-]*)\\/([0-9][\\w\\.-]*?)-SNAPSHOT\\/\\3-\\4-([0-9]{8}\\.[0-9]{6})-([0-9]+)(?:-([a-z]+))?\\.(\\w+)(?:\\.(\\w+))?$");

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
		VersionIdentifier versionIdentifier = VersionIdentifier.of(moduleIdentifier, versionBare, Optional.of("SNAPSHOT"), Optional.ofNullable(classifier), packaging);
		SnapshotIdentifier snapshotIdentifier = SnapshotIdentifier.of(versionIdentifier, timestamp, buildId);
		return new SnapshotLocation(repositoryName, snapshotIdentifier, hashMethod);
	}

	public Optional<Artifact> find(Repositories repositories) {
		return repositories.findArtifact(repositoryName, snapshotIdentifier);
	}

	public void add(Repositories repositories, ByteSource source, User uploader)
			throws UnauthorizedUserException {
		if(hashMethod != HashMethod.NONE) {
			return;
		}
		repositories.add(repositoryName, snapshotIdentifier, source, uploader);
	}

	public HttpResponse createResponse(Artifact artifact) {
		return HashedHttpResponse.of(artifact, hashMethod);
	}
}
