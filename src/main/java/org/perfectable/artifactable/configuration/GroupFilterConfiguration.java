package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Filter;
import org.perfectable.artifactable.GroupFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "GroupFilter", propOrder = {"groupId"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({GroupFilterConfiguration.class})
public class GroupFilterConfiguration implements FilterConfiguration {
	@XmlValue
	private String groupId;

	@Override
	public Filter build() {
		return GroupFilter.of(groupId);
	}
}
