package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement(name = "metadata")
public class Metadata {
	@XmlElement(name = "groupId")
	private String groupId;

	@XmlElement(name = "artifactId")
	private String artifactId;

	@XmlElement(name = "version")
	private Version version;

	@XmlElement(name = "versioning")
	private Versioning versioning = new Versioning();

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
}
