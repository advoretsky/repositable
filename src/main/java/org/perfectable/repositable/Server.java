package org.perfectable.repositable;

import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.configuration.ServerConfiguration;
import org.perfectable.webable.WebApplication;
import org.perfectable.webable.handler.HandlerServerConfigurationExtension;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;
import org.perfectable.webable.handler.authorization.BasicAuthenticationRequestChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class Server {
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

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

	public Server withRepositories(Repositories newRepositories) {
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
				.withHandler(VersionMetadataLocation.PATH_PATTERN, MetadataHandler.of(repositories, VersionMetadataLocation::fromPath))
				.withHandler(ModuleMetadataLocation.PATH_PATTERN, MetadataHandler.of(repositories, ModuleMetadataLocation::fromPath))
				.withHandler(ReleaseLocation.PATH_PATTERN, ArtifactHandler.of(repositories, ReleaseLocation::fromPath))
				.withHandler(SnapshotLocation.PATH_PATTERN, ArtifactHandler.of(repositories, SnapshotLocation::fromPath))
				.withRootHandler(RequestHandler.constant(HttpResponse.NOT_FOUND))
				.serveBlocking();
	}

	private static final int REQUIRED_ARGUMENTS_COUNT = 1;

	public static void main(String[] args) {
		LOGGER.info("Starting server; Working directory is {}", System.getProperty("user.dir"));
		if(args.length < REQUIRED_ARGUMENTS_COUNT) {
			System.out.println("Usage: repositable <configuration>"); // NOPMD actual use of system out
			return;
		}
		String configurationLocation = args[0];
		LOGGER.info("Reading configuration from {}", configurationLocation);
		File configurationFile = new File(configurationLocation);
		ServerConfiguration serverConfiguration;
		try(FileInputStream configurationStream = new FileInputStream(configurationFile)) {
			serverConfiguration = ServerConfiguration.parse(configurationStream);
		}
		catch (FileNotFoundException e) {
			System.out.println("No such file: " + configurationFile); // NOPMD actual use of system out
			return;
		}
		catch (IOException e) {
			System.out.println("Error reading file : " + configurationFile); // NOPMD actual use of system out
			e.printStackTrace(); // NOPMD actual use of system out
			return;
		}
		Server server = serverConfiguration.build();
		server.serve();
	}
}
