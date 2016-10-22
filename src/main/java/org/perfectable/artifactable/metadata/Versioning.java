package org.perfectable.artifactable.metadata;

import sun.java2d.pipe.SpanClipRenderer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Versioning {

	@XmlElementWrapper(name = "versions")
	@XmlElement(name = "version")
	private List<Version> versions = new LinkedList<>();

	@XmlElementWrapper(name = "snapshotVersions")
	@XmlElement(name = "snapshotVersion")
	private List<SnapshotVersion> snapshotVersions = new LinkedList<>();

	public void addVersion(String version) {
		versions.add(Version.of(version));
	}

	public void addSnapshotVersion(String packaging, String value, LocalDateTime timestamp) {
		snapshotVersions.add(SnapshotVersion.of("", packaging, value, timestamp)); // MARK classifier
	}
}
