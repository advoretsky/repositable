package org.perfectable.artifactable;

import org.perfectable.webable.WebApplication;

public final class Entry {

	public static void main(String[] args) {
		WebApplication.begin()
			.withRootHandler(ServerHandler.INSTANCE)
			.serveBlocking();
	}

	private Entry()
	{
		// entry point for execution
	}
}
