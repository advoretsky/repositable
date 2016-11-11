package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class VersionMetadataLocation implements MetadataLocation {

	// ex. "/libs-snapshot-local/org/perfectable/webable/1.1.0-SNAPSHOT/maven-metadata.xml" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)" + // repository
					"\\/([a-zA-Z][\\w\\/-]*)" + // groupId
					"\\/([a-zA-Z][\\w-]*)" + // artifactId
					"\\/([\\d\\.]+)(?:-([\\w\\.-]+))?" + // version
					"\\/maven-metadata\\.xml(?:\\.(\\w+))?"); // filename

	private static final String REPRESENTATION_FORMAT = "VersionMetadataLocation(%s, %s, %s)";

	private final String repositoryName;
	private final VersionIdentifier versionIdentifier;
	private final HashMethod hashMethod;

	public VersionMetadataLocation(String repositoryName, VersionIdentifier versionIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.versionIdentifier = versionIdentifier;
		this.hashMethod = hashMethod;
	}

	public static VersionMetadataLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace("/", ".");
		String artifactId = matcher.group(3);
		String versionBare = matcher.group(4);
		String versionQualifier = matcher.group(5);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(6));
		ModuleIdentifier moduleIdentifier =
				ModuleIdentifier.of(groupId, artifactId);
		VersionIdentifier versionIdentifier =
				VersionIdentifier.of(moduleIdentifier, versionBare, Optional.ofNullable(versionQualifier));
		return new VersionMetadataLocation(repositoryName, versionIdentifier, hashMethod);
	}

	@Override
	public Metadata fetch(RepositorySelector repositorySelector) {
		Repository repository = repositorySelector.select(repositoryName);
		return repository.fetchMetadata(versionIdentifier);
	}

	@Override
	public HttpResponse transformResponse(HttpResponse response) {
		return TransformedHttpResponse.of(response, hashMethod);
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, repositoryName, versionIdentifier.asBasePath(), hashMethod);
	}

}
