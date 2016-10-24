package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Metadata",
	propOrder = {"groupId", "artifactId", "version", "versioning"}) // plugins are ignored
public class Metadata {
	@SuppressWarnings("unused")
	@XmlElement(name = "groupId")
	private String groupId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "artifactId")
	private String artifactId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "version")
	private Version version; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "versioning")
	private Versioning versioning = Versioning.create(); // NOPMD is read only by JAXB

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void addVersion(String version) {
		versioning.addVersion(version);
	}

	public void addSnapshotVersion(String classifier, String extension, String version, int buildId, LocalDateTime timestamp) {
		versioning.addSnapshotVersion(classifier, extension, version, buildId, timestamp);
	}

	public void setVersion(String version) {
		this.version = Version.of(version);
	}
}
