package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlAccessorType(XmlAccessType.NONE)
public class SnapshotVersion {
	private String classifier;
	private String packaging;
	private String value; // NOPMD is read only by JAXB
	private LocalDateTime timestamp; // NOPMD is read only by JAXB

	public static SnapshotVersion of(String classifier, String packaging, String value, LocalDateTime timestamp) {
		SnapshotVersion snapshotVersion = new SnapshotVersion();
		snapshotVersion.setClassifier(classifier);
		snapshotVersion.setPackaging(packaging);
		snapshotVersion.setValue(value);
		snapshotVersion.setTimestamp(timestamp);
		return snapshotVersion;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "classifier")
	public String getClassifier() {
		return classifier;
	}

	private void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "extension")
	public String getPackaging() {
		return packaging;
	}

	private void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}

	@SuppressWarnings("unused")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	@XmlElement(name = "timestamp")
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
