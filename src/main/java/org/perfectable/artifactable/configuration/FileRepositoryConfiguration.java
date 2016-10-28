package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.FileRepository;
import org.perfectable.artifactable.Repositories;
import org.perfectable.artifactable.authorization.Group;
import org.perfectable.artifactable.authorization.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@XmlType(name = "FileRepository", propOrder = {"location", "users"})
@XmlRootElement(name = "repository")
@XmlAccessorType(XmlAccessType.NONE)
public class FileRepositoryConfiguration extends RepositoryConfiguration {
	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location")
	private Path location;

	@XmlJavaTypeAdapter(UserConfiguration.Reference.Adapter.class)
	@XmlElementWrapper(name = "uploaders")
	@XmlElement(name = "user")
	private Set<UserConfiguration> users;

	private transient FileRepository built;

	private FileRepository build() {
		if(built == null) {
			Set<User> uploaderSet = users.stream().map(UserConfiguration::build).collect(Collectors.toSet());
			Group uploaders = Group.of(uploaderSet);
			built = FileRepository.create(location, uploaders);
		}
		return built;
	}

	public Repositories appendTo(Repositories repositories) {
		FileRepository repository = build();
		return repositories.withAdditional(name, repository);
	}
}
