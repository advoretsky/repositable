package org.perfectable.artifactable;

import org.perfectable.webable.WebApplication;

public final class Entry {

	public static void main(String[] args) {
		Server server = new Server();
		WebApplication.begin()
			.withRootHandler(ServerHandler.of(server))
			.serveBlocking();
	}

	private Entry()
	{
		// entry point for execution
	}
}
