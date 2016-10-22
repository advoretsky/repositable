package org.perfectable.artifactable;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

class XmlPathAdapter extends XmlAdapter<String, Path> {
	@Override
	public Path unmarshal(String s) throws Exception {
		return Paths.get(s);
	}

	@Override
	public String marshal(Path path) throws Exception {
		return path.toString();
	}
}
