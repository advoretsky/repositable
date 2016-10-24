package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class ModuleMetadataLocation {
	// ex. "/libs-snapshot-local/org/perfectable/webable/maven-metadata.xml" // NOPMD
	static final Pattern PATH_PATTERN =
			Pattern.compile("\\/([a-zA-Z-]+)\\/([a-zA-Z][\\w\\/-]*)\\/([a-zA-Z][\\w-]*)\\/maven-metadata\\.xml(?:\\.(\\w+))?");

	private final String repositoryName;
	private final ModuleIdentifier moduleIdentifier;
	private final HashMethod hashMethod;

	public ModuleMetadataLocation(String repositoryName, ModuleIdentifier moduleIdentifier, HashMethod hashMethod) {
		this.repositoryName = repositoryName;
		this.moduleIdentifier = moduleIdentifier;
		this.hashMethod = hashMethod;
	}

	public static ModuleMetadataLocation fromPath(String path) {
		Matcher matcher = PATH_PATTERN.matcher(path);
		checkState(matcher.matches());
		String repositoryName = matcher.group(1);
		String groupId = matcher.group(2).replace("/", ".");
		String artifactId = matcher.group(3);
		HashMethod hashMethod = HashMethod.byExtension(matcher.group(4));
		ModuleIdentifier moduleIdentifier = ModuleIdentifier.of(groupId, artifactId);
		return new ModuleMetadataLocation(repositoryName, moduleIdentifier, hashMethod);
	}

	public Optional<Metadata> find(Repositories repositories) {
		return repositories.findMetadata(repositoryName, moduleIdentifier);
	}

	public MetadataHttpResponse createResponse(Metadata metadata) {
		return MetadataHttpResponse.of(metadata, hashMethod);
	}
}