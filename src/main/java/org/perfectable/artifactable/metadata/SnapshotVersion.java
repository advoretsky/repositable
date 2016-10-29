package org.perfectable.artifactable.metadata;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SnapshotVersion",
		propOrder = {"classifier", "extension", "value", "updated"})
public class SnapshotVersion {

	public static final Ordering<SnapshotVersion> COMPARATOR = Ordering.natural().onResultOf(SnapshotVersion::getUpdated).nullsLast();

	private String classifier;
	private String extension;
	private String value;
	private LocalDateTime updated;
	private int buildId;

	public static SnapshotVersion of(String classifier, String extension, String version, int buildId, LocalDateTime updated) {
		SnapshotVersion snapshotVersion = new SnapshotVersion();
		snapshotVersion.setClassifier(classifier);
		snapshotVersion.setExtension(extension);
		snapshotVersion.setValue(version);
		snapshotVersion.setBuildId(buildId);
		snapshotVersion.setUpdated(updated);
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
	public String getExtension() {
		return extension;
	}

	private void setExtension(String extension) {
		this.extension = extension;
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
	@XmlElement(name = "updated")
	public LocalDateTime getUpdated() {
		return updated;
	}

	private void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	@SuppressWarnings("unused")
	@XmlTransient
	public int getBuildId() {
		return buildId;
	}

	private void setBuildId(int buildId) {
		this.buildId = buildId;
	}


	public Snapshot toSnapshot() {
		return Snapshot.of(updated, buildId);
	}

	public static List<SnapshotVersion> merge(List<SnapshotVersion> first, List<SnapshotVersion> second) {
		Set<SnapshotVersion> merged = Sets.newHashSet(first);
		merged.addAll(second);
		return COMPARATOR.sortedCopy(merged);
	}
}
