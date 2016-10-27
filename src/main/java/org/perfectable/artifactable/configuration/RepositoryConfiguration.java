package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.FileRepository;
import org.perfectable.artifactable.Group;
import org.perfectable.artifactable.Server;
import org.perfectable.artifactable.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@XmlType(name = "Repository", propOrder = {"name", "location", "users"})
@XmlAccessorType(XmlAccessType.NONE)
public class RepositoryConfiguration {
	@XmlAttribute(name = "name")
	private String name;

	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location")
	private Path location;

	@XmlJavaTypeAdapter(UserConfiguration.Reference.Adapter.class)
	@XmlElementWrapper(name = "uploaders")
	@XmlElement(name = "user")
	private Set<UserConfiguration> users;

	public Server appendTo(Server server) {
		Set<User> uploaderSet = users.stream().map(UserConfiguration::build).collect(Collectors.toSet());
		Group uploaders = Group.of(uploaderSet);
		FileRepository repository = FileRepository.create(location, uploaders);
		return server.withRepository(name, repository);
	}
}
