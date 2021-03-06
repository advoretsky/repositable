package org.perfectable.repositable.configuration;

import org.perfectable.repositable.repository.Repositories;
import org.perfectable.repositable.repository.VirtualRepository;

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(name = "VirtualRepository", propOrder = {"sources"})
@XmlRootElement(name = "virtual")
@XmlAccessorType(XmlAccessType.NONE)
public class VirtualRepositoryConfiguration extends RepositoryConfiguration {
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "repository")
	@XmlJavaTypeAdapter(RepositoryConfiguration.Reference.Adapter.class)
	private Set<RepositoryConfiguration> sources;

	private transient VirtualRepository built;

	@Override
	protected VirtualRepository build() {
		if (built == null) {
			Repositories repositories = Repositories.create();
			for (RepositoryConfiguration source : sources) {
				repositories = source.appendTo(repositories);
			}
			built = VirtualRepository.create(repositories);
		}
		return built;
	}

}
