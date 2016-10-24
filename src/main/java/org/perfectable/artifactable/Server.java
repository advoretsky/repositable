package org.perfectable.artifactable;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import org.perfectable.artifactable.configuration.ServerConfiguration;
import org.perfectable.artifactable.metadata.Metadata;
import org.perfectable.webable.WebApplication;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Optional;

public final class Server {
	private final int port;

	private final ImmutableList<Repository> repositories;

	private Server(int port, ImmutableList<Repository> repositories) {
		this.port = port;
		this.repositories = repositories;
	}

	public Optional<Metadata> find(ArtifactMetadataLocation location) {
		return location.find(repositories);
	}

	public Optional<Metadata> find(VersionMetadataLocation location) {
		return location.find(repositories);
	}

	public Optional<Artifact> find(SnapshotLocation location) {
		return location.find(repositories);
	}

	public Optional<Artifact> find(ReleaseLocation location) {
		return location.find(repositories);
	}

	public void add(SnapshotLocation location, ByteSource source) {
		location.add(repositories, source);
	}

	public void add(ReleaseLocation location, ByteSource source) {
		location.add(repositories, source);
	}

	public static Server create(int port) {
		return new Server(port, ImmutableList.of());
	}

	public Server withRepository(Repository additionalRepository) {
		ImmutableList<Repository> newRepositories = ImmutableList.<Repository>builder()
				.addAll(repositories).add(additionalRepository).build();
		return new Server(port, newRepositories);
	}

	public void serve() {
		WebApplication.begin()
				.withPort(port)
				.withHandler(VersionMetadataLocation.PATH_PATTERN, VersionMetadataHandler.of(this))
				.withHandler(ArtifactMetadataLocation.PATH_PATTERN, ArtifactMetadataHandler.of(this))
				.withHandler(ReleaseLocation.PATH_PATTERN, ReleaseHandler.of(this))
				.withHandler(SnapshotLocation.PATH_PATTERN, SnapshotHandler.of(this))
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
