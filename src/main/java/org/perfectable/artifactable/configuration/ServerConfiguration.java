package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Repository;
import org.perfectable.artifactable.Server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "server")
public class ServerConfiguration {
	@XmlElement(name = "port", required = true)
	private int port;

	@XmlElement(name = "repository")
	private List<RepositoryConfiguration> repositories;

	public Server build() {
		Server server = Server.create(port);
		for (RepositoryConfiguration repositoryConfiguration : repositories) {
			Repository repository = repositoryConfiguration.build();
			server = server.withRepository(repository);
		}
		return server;

	}
}
