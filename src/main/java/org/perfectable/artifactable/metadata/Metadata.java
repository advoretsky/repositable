package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
public class Metadata {
	@SuppressWarnings("unused")
	@XmlElement(name = "groupId")
	private String groupId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "artifactId")
	private String artifactId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "version")
	private Version mainVersion; // NOPMD is read only by JAXB

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

	public void addSnapshotVersion(String packaging, String value, LocalDateTime timestamp) {
		versioning.addSnapshotVersion(packaging, value, timestamp);
	}

	public void setMainVersion(String mainVersion) {
		this.mainVersion = Version.of(mainVersion);
	}
}
