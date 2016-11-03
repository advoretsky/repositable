package org.perfectable.repositable.configuration;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.filter.GroupFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "GroupFilter", propOrder = {"groupId"})
@XmlAccessorType(XmlAccessType.NONE)
public class GroupFilterConfiguration implements FilterConfiguration {
	@XmlValue
	private String groupId;

	@Override
	public Filter build() {
		return GroupFilter.of(groupId);
	}
}
