package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Snapshot",
		propOrder = {"timestamp", "buildNumber", "localCopy"})
public class Snapshot {
	LocalDateTime timestamp;
	int buildNumber;
	boolean localCopy;

	public static Snapshot of(LocalDateTime timestamp, int buildNumber, boolean localCopy) {
		Snapshot snapshotVersion = new Snapshot();
		snapshotVersion.setTimestamp(timestamp);
		snapshotVersion.setBuildNumber(buildNumber);
		snapshotVersion.setLocalCopy(localCopy);
		return snapshotVersion;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "timestamp")
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	private void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "buildNumber")
	public int getBuildNumber() {
		return buildNumber;
	}

	private void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "localCopy")
	public boolean isLocalCopy() {
		return localCopy;
	}

	private void setLocalCopy(boolean localCopy) {
		this.localCopy = localCopy;
	}
}
