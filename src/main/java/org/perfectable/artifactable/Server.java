package org.perfectable.artifactable;

import org.perfectable.webable.handler.HandlerServerConfigurationExtension;
import org.perfectable.artifactable.configuration.ServerConfiguration;
import org.perfectable.webable.WebApplication;
import org.perfectable.webable.handler.authorization.BasicAuthenticationRequestChannel;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public final class Server {
	private final int port;
	private final Repositories repositories;
	private final Group users;

	private Server(int port, Repositories repositories, Group users) {
		this.port = port;
		this.repositories = repositories;
		this.users = users;
	}

	public static Server create(int port) {
		return new Server(port, Repositories.create(), Group.create());
	}

	public Server withRepository(String name, FileRepository additionalRepository) {
		Repositories newRepositories = repositories.withAdditional(name, additionalRepository);
		return new Server(port, newRepositories, users);
	}

	public Server withUser(User user) {
		Group newUsers = users.join(user);
		return new Server(port, repositories, newUsers);
	}

	public void serve() {
		StoringRequestAuthenticator requestAuthenticator = StoringRequestAuthenticator.allowing(users);
		WebApplication.begin()
				.withPort(port)
				.extend(HandlerServerConfigurationExtension.create())
				.withGlobalChannel(BasicAuthenticationRequestChannel.of(requestAuthenticator))
				.withHandler(VersionMetadataLocation.PATH_PATTERN, VersionMetadataHandler.of(repositories))
				.withHandler(ModuleMetadataLocation.PATH_PATTERN, ModuleMetadataHandler.of(repositories))
				.withHandler(ReleaseLocation.PATH_PATTERN, ReleaseHandler.of(repositories))
				.withHandler(SnapshotLocation.PATH_PATTERN, SnapshotHandler.of(repositories))
				.withRootHandler(RequestHandler.constant(HttpResponse.NOT_FOUND))
				.serveBlocking();
	}

	private static final int REQUIRED_ARGUMENTS_COUNT = 1;

	public static void main(String[] args) throws JAXBException {
		if(args.length < REQUIRED_ARGUMENTS_COUNT) {
			System.out.println("Usage: artifactable <configuration>"); // NOPMD actual use of system out
			return;
		}
		String configurationLocation = args[0];
		File configurationFile = new File(configurationLocation);
		JAXBContext jaxbContext = JAXBContext.newInstance(ServerConfiguration.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ServerConfiguration serverConfiguration = (ServerConfiguration) jaxbUnmarshaller.unmarshal(configurationFile);
		Server server = serverConfiguration.build();
		server.serve();
	}

}
