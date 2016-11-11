package org.perfectable.repositable.configuration;

import org.perfectable.repositable.Server;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.repository.Repositories;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
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

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Server", propOrder = {"port", "users", "repositories"})
@XmlRootElement(name = "server")
public class ServerConfiguration {
	private static final int NOT_CONFIGURED = -1;

	@XmlElement(name = "port")
	private int port = NOT_CONFIGURED; // SUPPRESS - cannot be final, injected by JAXB

	@XmlElementWrapper(name = "users")
	@XmlElement(name = "user")
	private List<UserConfiguration> users = new LinkedList<>(); // SUPPRESS - cannot be final, injected by JAXB

	@XmlElements({
			@XmlElement(name = "repository", type = FileRepositoryConfiguration.class),
			@XmlElement(name = "virtual", type = VirtualRepositoryConfiguration.class)
	})
	private List<RepositoryConfiguration> repositories = new LinkedList<>(); // SUPPRESS - injected by JAXB

	public Server build() {
		Server server = Server.create();
		if (port != NOT_CONFIGURED) {
			server = server.withPort(port);
		}
		Group loggableUsers = Group.create();
		for (UserConfiguration user : users) {
			loggableUsers = user.appendTo(loggableUsers);
		}
		server = server.withLoggableUser(loggableUsers);
		Repositories builtRepositories = Repositories.create();
		for (RepositoryConfiguration repositoryConfiguration : this.repositories) {
			builtRepositories = repositoryConfiguration.appendTo(builtRepositories);
		}
		server = server.withRepositories(builtRepositories);
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
