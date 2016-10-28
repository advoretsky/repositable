package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Repositories;
import org.perfectable.artifactable.Server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Server", propOrder = {"port", "users", "repositories"})
@XmlRootElement(name = "server")
public class ServerConfiguration {
	@XmlElement(name = "port", required = true)
	private int port;

	@XmlElementWrapper(name = "users")
	@XmlElement(name = "user")
	private List<UserConfiguration> users;

	@XmlElements({
			@XmlElement(name="repository", type=FileRepositoryConfiguration.class),
			@XmlElement(name="virtual", type= VirtualRepositoryConfiguration.class)
	})
	private List<RepositoryConfiguration> repositories;

	public Server build() {
		Server server = Server.create(port);
		for (UserConfiguration user : users) {
			server = user.appendTo(server);
		}
		Repositories repositories = Repositories.create();
		for (RepositoryConfiguration repositoryConfiguration : this.repositories) {
			repositories = repositoryConfiguration.appendTo(repositories);
		}
		server = server.withRepositories(repositories);
		return server;

	}
}
