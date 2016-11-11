package org.perfectable.repositable.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.bind.annotation.adapters.XmlAdapter;

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
