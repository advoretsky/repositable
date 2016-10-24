package org.perfectable.artifactable.configuration;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

class XmlPathAdapter extends XmlAdapter<String, Path> {
	@Override
	public Path unmarshal(String pathRepresentation) {
		return Paths.get(pathRepresentation);
	}

	@Override
	public String marshal(Path path) {
		return path.toString();
	}
}
