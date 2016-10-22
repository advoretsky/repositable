package org.perfectable.artifactable;

import javax.xml.bind.JAXBException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public final class Main {

	public static void main(String[] args) throws JAXBException {
		if(args.length < 1) {
			System.out.println("Usage: artifactable <configuration>");
			return;
		}
		String configurationLocation = args[0];
		Server server = ServerParser.of(configurationLocation)
				.parse();
		server.serve();
	}

	private Main()
	{
		// entry point for execution
	}
}
