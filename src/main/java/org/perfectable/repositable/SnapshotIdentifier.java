package org.perfectable.repositable;

import org.perfectable.repositable.metadata.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Ordering;

import static com.google.common.base.Preconditions.checkState;

public final class SnapshotIdentifier implements ArtifactIdentifier {

	private static final Pattern SUFFIX_PATTERN =
			Pattern.compile("([0-9]{8}\\.[0-9]{6})-([0-9]+)(?:-([a-z-]+))?\\.(\\w+)$");

	static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss");

	private final PackageIdentifier packageIdentifier;
	private final LocalDateTime timestamp;
	private final int buildId;

	private SnapshotIdentifier(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId) {
		this.packageIdentifier = packageIdentifier;
		this.timestamp = timestamp;
		this.buildId = buildId;
	}

	public static SnapshotIdentifier of(PackageIdentifier packageIdentifier, LocalDateTime timestamp, int buildId) {
		return new SnapshotIdentifier(packageIdentifier, timestamp, buildId);
	}

	public static SnapshotIdentifier ofEntry(VersionIdentifier versionIdentifier, String entry) {
		String baseName = versionIdentifier.asFileBaseName();
		checkState(entry.startsWith(baseName));
		String suffix = entry.substring(baseName.length() + 1);
		Matcher matcher = SUFFIX_PATTERN.matcher(suffix);
		checkState(matcher.matches());
		LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), TIMESTAMP_FORMATTER); // SUPPRESS MagicNumber
		int buildId = Integer.parseInt(matcher.group(2)); // SUPPRESS MagicNumber
		String classifier = matcher.group(3); // SUPPRESS MagicNumber
		String packaging = matcher.group(4); // SUPPRESS MagicNumber
		PackageIdentifier packageIdentifier =
				PackageIdentifier.of(versionIdentifier, Optional.ofNullable(classifier), packaging);
		return of(packageIdentifier, timestamp, buildId);
	}

	@Override
	public Path asBasePath() {
		return packageIdentifier.asBasePath();
	}

	Path asBuildPath() {
		return packageIdentifier.asBuildPath(timestamp, buildId);
	}

	@Override
	public Path asFetchPath(EntryLister lister) {
		return asBuildPath();
	}

	@Override
	public Path asUploadPath(BuildGenerator buildGenerator) {
		return asBuildPath();
	}

	public void appendVersion(Metadata metadata) {
		packageIdentifier.addSnapshotVersion(metadata, timestamp, buildId);
	}

	@Override
	public boolean matches(Filter filter) {
		return filter.matchesSnapshot(packageIdentifier, timestamp, buildId);
	}

	public static SnapshotIdentifier newest(List<SnapshotIdentifier> candidates) {
		return Ordering.natural().<SnapshotIdentifier>onResultOf(candidate -> candidate.timestamp).max(candidates);
	}
}
