package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Server;
import org.perfectable.artifactable.FileRepository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.nio.file.Path;

@XmlAccessorType(XmlAccessType.NONE)
public class RepositoryConfiguration {
	@XmlAttribute(name = "name")
	private String name;

	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location")
	private Path location;

	public Server appendTo(Server server) {
		FileRepository repository = FileRepository.create(location);
		return server.withRepository(name, repository);
	}
}
