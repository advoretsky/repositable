package org.perfectable.artifactable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public final class ServerParser {
	private final String configurationLocation;

	public static ServerParser of(String configurationLocation) {
		return new ServerParser(configurationLocation);
	}

	private ServerParser(String configurationLocation) {
		this.configurationLocation = configurationLocation;
	}

	public Server parse() throws JAXBException {
		File configurationFile = new File(configurationLocation);
		JAXBContext jaxbContext = JAXBContext.newInstance(Server.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Server server = (Server) jaxbUnmarshaller.unmarshal(configurationFile);
		return server;
	}
}
