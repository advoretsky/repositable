package org.perfectable.artifactable;

import org.perfectable.artifactable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class SnapshotIdentifier implements ArtifactIdentifier {

	private static final Pattern SUFFIX_PATTERN = Pattern.compile("([0-9]{8}\\.[0-9]{6})-([0-9]+)(?:-([a-z]+))?\\.(\\w+)$");

	static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss");

	private final VersionIdentifier versionIdentifier;
	private final LocalDateTime timestamp;
	private final int buildId;

	public SnapshotIdentifier(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId) {
		this.versionIdentifier = versionIdentifier;
		this.timestamp = timestamp;
		this.buildId = buildId;
	}

	public static SnapshotIdentifier of(VersionIdentifier versionIdentifier, LocalDateTime timestamp, int buildId) {
		return new SnapshotIdentifier(versionIdentifier, timestamp, buildId);
	}

	public static SnapshotIdentifier ofEntry(VersionIdentifier versionIdentifier, Path nested) {
		Path entryPath = nested.subpath(1,nested.getNameCount());
		entryPath = versionIdentifier.asFilePath().relativize(entryPath);
		Path filePath = checkNotNull(entryPath.getFileName());
		String fileName = filePath.toString();
		String baseName = versionIdentifier.fileBaseName();
		checkState(fileName.startsWith(baseName));
		String suffix = fileName.substring(baseName.length() + 1);
		Matcher matcher = SUFFIX_PATTERN.matcher(suffix);
		checkState(matcher.matches());
		LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), TIMESTAMP_FORMATTER);
		int buildId = Integer.parseInt(matcher.group(2));
		String classifier = matcher.group(3);
		String packaging = matcher.group(4);
		VersionIdentifier actualVersionIdentifier = versionIdentifier.withPackaging(packaging);
		if(classifier != null) {
			actualVersionIdentifier = actualVersionIdentifier.withClassifier(classifier);
		}
		return of(actualVersionIdentifier, timestamp, buildId);
	}

	@Override
	public Path asFilePath() {
		return versionIdentifier.asSnapshotPath(timestamp, buildId);
	}

	public void appendVersion(Metadata metadata) {
		versionIdentifier.addSnapshotVersion(metadata, timestamp, buildId);
	}
}
