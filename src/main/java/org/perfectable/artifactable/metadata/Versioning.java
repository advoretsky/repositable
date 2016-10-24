package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class Versioning {
	private List<Version> versions;
	private List<SnapshotVersion> snapshotVersions;

	public static Versioning create() {
		Versioning versioning = new Versioning();
		versioning.setVersions(new LinkedList<>());
		versioning.setSnapshotVersions(new LinkedList<>());
		return versioning;
	}

	public void addVersion(String version) {
		versions.add(Version.of(version));
	}

	public void addSnapshotVersion(String packaging, String value, LocalDateTime timestamp) {
		snapshotVersions.add(SnapshotVersion.of("", packaging, value, timestamp)); // MARK classifier
	}

	@SuppressWarnings("unused")
	@XmlElementWrapper(name = "snapshotVersions")
	@XmlElement(name = "snapshotVersion")
	public List<SnapshotVersion> getSnapshotVersions() {
		return snapshotVersions;
	}

	private void setSnapshotVersions(List<SnapshotVersion> snapshotVersions) {
		this.snapshotVersions = snapshotVersions;
	}

	@SuppressWarnings("unused")
	@XmlElementWrapper(name = "versions")
	@XmlElement(name = "version")
	public List<Version> getVersions() {
		return versions;
	}

	private void setVersions(List<Version> versions) {
		this.versions = versions;
	}
}
