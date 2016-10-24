package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public class Version {
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
