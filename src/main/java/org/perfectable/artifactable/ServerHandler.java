package org.perfectable.artifactable;

import com.google.common.net.MediaType;
import org.perfectable.webable.handler.HttpRequest;
import org.perfectable.webable.handler.HttpResponse;
import org.perfectable.webable.handler.RequestHandler;

import java.nio.charset.StandardCharsets;

public class ServerHandler implements RequestHandler {
	public static final ServerHandler INSTANCE = new ServerHandler();

	@Override
	public HttpResponse handle(HttpRequest request) {
		return HttpResponse.constant(MediaType.PLAIN_TEXT_UTF_8, "Found".getBytes(StandardCharsets.UTF_8));
	}
}
