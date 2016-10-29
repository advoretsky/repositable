package org.perfectable.artifactable.metadata;

import com.google.common.collect.Ordering;
import org.perfectable.artifactable.CompositeFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Versioning",
		propOrder = {"latest", "release", "snapshot", "versions", "lastUpdated", "snapshotVersions"})
public class Versioning {
	private static final Ordering<LocalDateTime> TIMESTAMP_COMPARATOR = Ordering.natural().nullsFirst();

	private Version latest;
	private Version release;
	private Snapshot snapshot;
	private List<Version> versions;
	private LocalDateTime lastUpdated;
	private List<SnapshotVersion> snapshotVersions;

	public static Versioning create() {
		Versioning versioning = new Versioning();
		versioning.setVersions(new LinkedList<>());
		versioning.setSnapshotVersions(new LinkedList<>());
		return versioning;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "latest")
	public Version getLatest() {
		return latest;
	}

	private void setLatest(Version latest) {
		this.latest = latest;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "release")
	public Version getRelease() {
		return release;
	}

	@SuppressWarnings("unused")
	private void setRelease(Version release) {
		this.release = release;
	}

	@SuppressWarnings("unused")
	@XmlElement(name = "snapshot")
	public Snapshot getSnapshot() {
		return snapshot;
	}

	private void setSnapshot(Snapshot snapshot) {
		this.snapshot = snapshot;
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

	@SuppressWarnings("unused")
	@XmlElement(name = "lastUpdated")
	@XmlJavaTypeAdapter(value = TimestampAdapter.class)
	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	@SuppressWarnings("unused")
	private void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
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

	public void addVersion(String version) {
		versions.add(Version.of(version));
		versions.sort(Version.COMPARATOR.reversed());
		setLatest(versions.get(0));
	}

	public void addSnapshotVersion(String classifier, String extension, String version, int buildId, LocalDateTime timestamp) {
		snapshotVersions.add(SnapshotVersion.of(classifier, extension, version, buildId, timestamp));
		snapshotVersions.sort(SnapshotVersion.COMPARATOR.reversed());
		setSnapshot(snapshotVersions.get(0).toSnapshot());
		LocalDateTime newLastUpdated = TIMESTAMP_COMPARATOR.compare(timestamp, lastUpdated) > 0 ? timestamp : lastUpdated;
		setLastUpdated(newLastUpdated);
	}

	public Versioning merge(Versioning other) {
		Versioning result = new Versioning();
		result.setLatest(Version.latest(other.latest, latest));
		result.setRelease(Version.latest(other.release, release));
		result.setSnapshot(Snapshot.latest(other.snapshot, snapshot));
		result.setVersions(Version.merge(other.versions, versions));
		LocalDateTime newLastUpdated = TIMESTAMP_COMPARATOR.compare(other.lastUpdated, lastUpdated) > 0  ? other.lastUpdated : lastUpdated;
		result.setLastUpdated(newLastUpdated);
		result.setSnapshotVersions(SnapshotVersion.merge(other.snapshotVersions, snapshotVersions));
		return result;
	}
}
