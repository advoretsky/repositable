package org.perfectable.artifactable;

import com.google.common.io.ByteSource;
import org.perfectable.webable.handler.HttpResponse;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class ReleaseLocation {

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

	public Optional<Artifact> find(List<Repository> repositories) {
		Optional<Repository> selectedRepositoryOption = Repository.selectByName(repositories, repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return Optional.empty();
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		return selectedRepository.findArtifact(versionIdentifier);
	}

	public void add(List<Repository> repositories, ByteSource source) {
		Optional<Repository> selectedRepositoryOption = Repository.selectByName(repositories, repositoryName);
		if(!selectedRepositoryOption.isPresent()) {
			return; // MARK return not found
		}
		Repository selectedRepository = selectedRepositoryOption.get();
		Artifact artifact = Artifact.of(versionIdentifier, source);
		selectedRepository.put(artifact);

	}

	public HttpResponse createResponse(Artifact artifact) {
		return ArtifactHttpResponse.of(artifact, hashMethod);
	}

	public boolean allowsAdding() {
		return hashMethod == HashMethod.NONE;
	}
}
