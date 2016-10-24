package org.perfectable.artifactable;

import javax.xml.bind.JAXBException;

public final class Main {

	private static final int REQUIRED_ARGUMENTS_COUNT = 1;

	public static void main(String[] args) throws JAXBException {
		if(args.length < REQUIRED_ARGUMENTS_COUNT) {
			System.out.println("Usage: artifactable <configuration>"); // NOPMD actual use of system out
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
