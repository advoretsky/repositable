package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;
import java.util.Comparator;

@XmlAccessorType(XmlAccessType.NONE)
public class Version {
	public static final Comparator<? super Version> COMPARATOR = Comparator.comparing(Version::getValue);

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
}
