package org.perfectable.artifactable;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class SnapshotIdentifier implements FileIdentifier {

	private static final Pattern SUFFIX_PATTERN = Pattern.compile("([0-9]{8}\\.[0-9]{6})-([0-9]+)(?:-([a-z]+))?\\.(\\w+)$");

	static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss");

	final VersionIdentifier versionIdentifier;
	final LocalDateTime timestamp;
	final String buildId;

	public SnapshotIdentifier(VersionIdentifier versionIdentifier, LocalDateTime timestamp, String buildId) {
		this.versionIdentifier = versionIdentifier;
		this.timestamp = timestamp;
		this.buildId = buildId;
	}

	public static SnapshotIdentifier of(VersionIdentifier versionIdentifier, LocalDateTime timestamp, String buildId) {
		return new SnapshotIdentifier(versionIdentifier, timestamp, buildId);
	}

	public static SnapshotIdentifier ofEntry(VersionIdentifier versionIdentifier, Path nested) {
		Path entryPath = nested.subpath(1,nested.getNameCount());
		entryPath = versionIdentifier.asFilePath().relativize(entryPath);
		String fileName = entryPath.getFileName().toString();
		String baseName = versionIdentifier.artifactIdentifier.artifactId + "-" + versionIdentifier.versionBare;
		checkState(fileName.startsWith(baseName));
		String suffix = fileName.substring(baseName.length() + 1);
		Matcher matcher = SUFFIX_PATTERN.matcher(suffix);
		checkState(matcher.matches());
		LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), TIMESTAMP_FORMATTER);
		String buildId = matcher.group(2);
		String classifier = matcher.group(3);
		String packaging = matcher.group(4);
		VersionIdentifier actualVersionIdentifier = versionIdentifier.withPackaging(packaging).withClassifier(classifier);
		return of(actualVersionIdentifier, timestamp, buildId);
	}

	@Override
	public Path asFilePath() {
		String timestampString = TIMESTAMP_FORMATTER.format(timestamp);
		Path artifactPath = versionIdentifier.asBasePath();
		String classifierSuffix = versionIdentifier.classifier == null ? "" : "-" + versionIdentifier.classifier;
		String filePath = versionIdentifier.artifactIdentifier.artifactId + "-" + versionIdentifier.versionBare + "-" + timestampString + "-" + buildId + classifierSuffix + "." + versionIdentifier.packaging;
		return artifactPath.resolve(filePath);
	}
}
