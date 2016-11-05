package org.perfectable.repositable.configuration;

import org.perfectable.repositable.Repositories;
import org.perfectable.repositable.Server;
import org.perfectable.repositable.authorization.Group;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.InputStream;
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
		Server server = Server.create()
			.withPort(port);
		Group loggableUsers = Group.create();
		for (UserConfiguration user : users) {
			loggableUsers = user.appendTo(loggableUsers);
		}
		server = server.withLoggableUser(loggableUsers);
		Repositories repositories = Repositories.create();
		for (RepositoryConfiguration repositoryConfiguration : this.repositories) {
			repositories = repositoryConfiguration.appendTo(repositories);
		}
		server = server.withRepositories(repositories);
		return server;

	}

	public static ServerConfiguration parse(InputStream configurationStream) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (ServerConfiguration) jaxbUnmarshaller.unmarshal(configurationStream);
		}
		catch (JAXBException e) {
			throw new AssertionError(e);
		}

	}
}
