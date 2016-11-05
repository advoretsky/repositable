package org.perfectable.repositable.configuration;

import org.perfectable.repositable.FileRepository;
import org.perfectable.repositable.Filter;
import org.perfectable.repositable.Repository;
import org.perfectable.repositable.authorization.Group;
import org.perfectable.repositable.authorization.User;
import org.perfectable.repositable.filter.ConjunctionFilter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@XmlType(name = "FileRepository", propOrder = {"location", "filters", "users"})
@XmlRootElement(name = "repository")
@XmlAccessorType(XmlAccessType.NONE)
public class FileRepositoryConfiguration extends RepositoryConfiguration {
	@XmlJavaTypeAdapter(value = XmlPathAdapter.class)
	@XmlElement(name = "location", required = true)
	private Path location;

	@XmlElementWrapper(name = "filters")
	@XmlElements({
			@XmlElement(name = "group", type = GroupFilterConfiguration.class),
			@XmlElement(name = "snapshots", type = SnapshotFilterConfiguration.class),
			@XmlElement(name = "releases", type = ReleaseFilterConfiguration.class)
	})
	private List<FilterConfiguration> filters = new LinkedList<>(); // NOPMD cant be final, injected by jaxb

	@XmlJavaTypeAdapter(UserConfiguration.Reference.Adapter.class)
	@XmlElementWrapper(name = "uploaders")
	@XmlElement(name = "user")
	private Set<UserConfiguration> users = new HashSet<>(); // NOPMD cant be final, injected by jaxb

	private transient Repository built;

	protected Repository build() {
		if(built == null) {
			Set<Filter> filterSet = filters.stream().map(FilterConfiguration::build).collect(Collectors.toSet());
			Filter filter = ConjunctionFilter.of(filterSet);
			Set<User> uploaderSet = users.stream().map(UserConfiguration::build).collect(Collectors.toSet());
			Group uploaders = Group.of(uploaderSet);
			built = FileRepository.create(location).filtered(filter).restrictUploaders(uploaders);
		}
		return built;
	}


}
