package org.perfectable.repositable;

import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.configuration.ServerConfiguration;
import org.perfectable.repositable.repository.Repositories;
import org.perfectable.webable.ServerConfigurator;
import org.perfectable.webable.ServerMonitor;
import org.perfectable.webable.WebApplication;
import org.perfectable.webable.handler.HandlerServerConfigurationExtension;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;
import org.perfectable.webable.handler.authorization.BasicAuthenticationRequestChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.common.base.StandardSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Server {
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private static final int DEFAULT_PORT = 8080;

	private final int port;
	private final RepositorySelector repositorySelector;
	private final Group loggableUsers;

	private Server(int port, RepositorySelector repositorySelector, Group loggableUsers) {
		this.port = port;
		this.repositorySelector = repositorySelector;
		this.loggableUsers = loggableUsers;
	}

	public static Server create() {
		return new Server(DEFAULT_PORT, Repositories.create(), Group.create());
	}

	public Server withPort(int newPort) {
		return new Server(newPort, repositorySelector, loggableUsers);
	}

	public Server withRepositories(RepositorySelector repositorySelector) {
		return new Server(port, repositorySelector, loggableUsers);
	}

	public Server withLoggableUser(Group newLoggableUsers) {
		return new Server(port, repositorySelector, newLoggableUsers);
	}

	private ServerConfigurator<?> createServerConfiguration() {
		StoringRequestAuthenticator requestAuthenticator = StoringRequestAuthenticator.allowing(loggableUsers);
		return WebApplication.begin()
				.withPort(port)
				.extend(HandlerServerConfigurationExtension.create())
				.withGlobalChannel(BasicAuthenticationRequestChannel.of(requestAuthenticator))
				.withHandler(VersionMetadataLocation.PATH_PATTERN,
						MetadataHandler.of(repositorySelector, VersionMetadataLocation::fromPath))
				.withHandler(ModuleMetadataLocation.PATH_PATTERN,
						MetadataHandler.of(repositorySelector, ModuleMetadataLocation::fromPath))
				.withHandler(PackageLocation.PATH_PATTERN,
						ArtifactHandler.of(repositorySelector, PackageLocation::fromPath))
				.withHandler(SnapshotLocation.PATH_PATTERN,
						ArtifactHandler.of(repositorySelector, SnapshotLocation::fromPath))
				.withRootHandler(RequestHandler.constant(HttpResponse.NOT_FOUND));
	}

	public Monitor serve() {
		ServerMonitor serverMonitor = createServerConfiguration()
				.serve();
		return new MonitorWrapper(serverMonitor);
	}

	public void serveBlocking() {
		createServerConfiguration()
				.serveBlocking();
	}

	private static final int REQUIRED_ARGUMENTS_COUNT = 1;

	public static void main(String[] args) throws IOException {
		String workingDirectory = StandardSystemProperty.USER_DIR.value();
		LOGGER.info("Starting server; Working directory is {}", workingDirectory);
		if(args.length < REQUIRED_ARGUMENTS_COUNT) {
			LOGGER.error("No configuration file provided");
			return;
		}
		ServerConfiguration serverConfiguration = parseConfiguration(args[0]);
		Server server = serverConfiguration.build();
		server.serveBlocking();
	}

	public static ServerConfiguration parseConfiguration(String configurationLocation) throws IOException {
		LOGGER.info("Reading configuration from {}", configurationLocation);
		File configurationFile = new File(configurationLocation);
		try(FileInputStream configurationStream = new FileInputStream(configurationFile)) {
			return ServerConfiguration.parse(configurationStream);
		}
		catch (FileNotFoundException e) {
			LOGGER.error("No such file: {}", configurationFile, e); // NOPMD pmd counts 2 arguments needed
			throw e;
		}
		catch (IOException e) {
			LOGGER.error("Error reading file: {}", configurationFile, e); // NOPMD pmd counts 2 arguments needed
			throw e;
		}
	}

	interface Monitor extends AutoCloseable {
		@Override
		void close();
	}

	private static class MonitorWrapper implements Monitor {
		private final ServerMonitor serverMonitor;

		public MonitorWrapper(ServerMonitor serverMonitor) {
			this.serverMonitor = serverMonitor;
		}

		@Override
		public void close() {
			serverMonitor.close();
		}
	}
}
