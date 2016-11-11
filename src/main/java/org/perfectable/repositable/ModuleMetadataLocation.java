package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;
import org.perfectable.webable.handler.HttpResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public final class ModuleMetadataLocation implements MetadataLocation {
	// ex. "/libs-snapshot-local/org/perfectable/webable/maven-metadata.xml"
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)" + // repository
					"\\/([a-zA-Z][\\w\\/-]*)" + // groupId
					"\\/([a-zA-Z][\\w-]*)" + // artifactId
					"\\/maven-metadata\\.xml(?:\\.(\\w+))?");

	private static final String REPRESENTATION_FORMAT = "ModuleMetadataLocation(%s, %s, %s)";

	private final String repositoryName;
	private final ModuleIdentifier moduleIdentifier;
	private final HashMethod hashMethod;

	private ModuleMetadataLocation(String repositoryName, ModuleIdentifier moduleIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.moduleIdentifier = moduleIdentifier;
		this.hashMethod = hashMethod;
	}

	public static ModuleMetadataLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1); // SUPPRESS MagicNumber
		String groupId = matcher.group(2).replace("/", "."); // SUPPRESS MagicNumber
		String artifactId = matcher.group(3); // SUPPRESS MagicNumber
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(4)); // SUPPRESS MagicNumber
		ModuleIdentifier moduleIdentifier = ModuleIdentifier.of(groupId, artifactId);
		return new ModuleMetadataLocation(repositoryName, moduleIdentifier, hashMethod);
	}

	@Override
	public Metadata fetch(RepositorySelector repositorySelector) {
		Repository targetRepository = repositorySelector.select(repositoryName);
		return targetRepository.fetchMetadata(moduleIdentifier);
	}

	@Override
	public HttpResponse transformResponse(HttpResponse response) {
		return TransformedHttpResponse.of(response, hashMethod);
	}

	@Override
	public String toString() {
		return String.format(REPRESENTATION_FORMAT, repositoryName, moduleIdentifier.asBasePath(), hashMethod);
	}
}
