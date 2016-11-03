package org.perfectable.repositable.configuration;

import org.perfectable.repositable.Filter;
import org.perfectable.repositable.filter.StabilityFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReleaseFilter", propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
public class ReleaseFilterConfiguration implements FilterConfiguration {
	@Override
	public Filter build() {
		return StabilityFilter.RELEASE;
	}
}
