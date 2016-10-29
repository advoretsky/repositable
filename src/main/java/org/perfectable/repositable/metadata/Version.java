package org.perfectable.repositable.metadata;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.NONE)
public class Version {
	public static final Ordering<? super Version> COMPARATOR = Ordering.natural().onResultOf(Version::getValue).nullsLast();

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

	public static Version latest(Version first, Version... more) {
		Version best = first;
		for(Version candidate : more) {
			if(COMPARATOR.compare(candidate, best) > 0) {
				best = candidate;
			}
		}
		return best;
	}

	public static List<Version> merge(List<Version> first, List<Version> second) {
		Set<Version> merged = Sets.newHashSet(first);
		merged.addAll(second);
		return COMPARATOR.sortedCopy(merged);
	}
}
