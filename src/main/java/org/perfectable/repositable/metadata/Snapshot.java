package org.perfectable.repositable.metadata;

import java.time.LocalDateTime;
import java.util.Comparator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Snapshot",
		propOrder = {"timestamp", "buildNumber"})
public class Snapshot {
	private LocalDateTime timestamp;
	private int buildNumber;

	public static final Comparator<? super Snapshot> COMPARATOR =
			Comparator.nullsFirst(Comparator.comparing(Snapshot::getTimestamp));

	public static Snapshot of(LocalDateTime timestamp, int buildNumber) {
		Snapshot snapshotVersion = new Snapshot();
		snapshotVersion.setTimestamp(timestamp);
		snapshotVersion.setBuildNumber(buildNumber);
		return snapshotVersion;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "timestamp")
	@XmlJavaTypeAdapter(TimestampAdapter.WithSeparator.class)
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

	public static Snapshot latest(Snapshot first, Snapshot... more) {
		Snapshot best = first;
		for(Snapshot candidate : more) {
			if(COMPARATOR.compare(candidate, best) > 0) {
				best = candidate;
			}
		}
		return best;
	}
}
