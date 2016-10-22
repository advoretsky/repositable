package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.XmlValue;

public class Version {
	@XmlValue
	private String value;

	public static Version of(String value) {
		Version version = new Version();
		version.value = value;
		return version;
	}
}
