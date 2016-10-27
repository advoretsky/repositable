package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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

	@XmlElement(name = "repository")
	private List<RepositoryConfiguration> repositories;

	public Server build() {
		Server server = Server.create(port);
		for (UserConfiguration user : users) {
			server = user.appendTo(server);
		}
		for (RepositoryConfiguration repositoryConfiguration : repositories) {
			server = repositoryConfiguration.appendTo(server);
		}
		return server;

	}
}
