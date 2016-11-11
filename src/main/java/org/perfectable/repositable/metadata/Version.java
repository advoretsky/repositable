package org.perfectable.repositable.metadata;

import java.util.Collection;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.collect.Ordering;

@XmlAccessorType(XmlAccessType.NONE)
public class Version {
	public static final Ordering<? super Version> COMPARATOR =
			Ordering.natural().onResultOf(Version::getValue).nullsFirst();

	private String value;

	public static Version of(String value) {
		Version version = new Version();
		version.value = value;
		return version;
	}

	@SuppressWarnings("unused")
	@XmlValue
	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Version)) {
			return false;
		}
		Version other = (Version) obj;
		return Objects.equals(value, other.value);
	}

	public static Version latest(Version first, Version... more) {
		Version best = first;
		for (Version candidate : more) {
			if (COMPARATOR.compare(candidate, best) > 0) {
				best = candidate;
			}
		}
		return best;
	}

	public static SortedSet<Version> merge(Collection<Version> first, Collection<Version> second) {
		SortedSet<Version> merged = new TreeSet<>(COMPARATOR.reversed());
		merged.addAll(first);
		merged.addAll(second);
		return merged;
	}
}
