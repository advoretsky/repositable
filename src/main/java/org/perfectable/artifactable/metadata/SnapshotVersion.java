package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

import static com.oracle.jrockit.jfr.ContentType.Timestamp;

public class SnapshotVersion {
	@XmlElement(name = "classifier")
	private String classifier;

	@XmlElement(name = "extension")
	private String packaging;

	@XmlElement(name = "value")
	private String value;

	@XmlJavaTypeAdapter(TimestampAdapter.class)
	@XmlElement(name = "timestamp")
	private LocalDateTime timestamp;

	public static SnapshotVersion of(String classifier, String packaging, String value, LocalDateTime timestamp) {
		SnapshotVersion snapshotVersion = new SnapshotVersion();
		snapshotVersion.setClassifier(classifier);
		snapshotVersion.setPackaging(packaging);
		snapshotVersion.setValue(value);
		snapshotVersion.setTimestamp(timestamp);
		return snapshotVersion;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
