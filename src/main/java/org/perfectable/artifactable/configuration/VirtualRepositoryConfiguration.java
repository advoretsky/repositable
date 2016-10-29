package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Repositories;
import org.perfectable.artifactable.VirtualRepository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Set;

@XmlType(name = "VirtualRepository", propOrder = {"sources"})
@XmlRootElement(name = "virtual")
@XmlAccessorType(XmlAccessType.NONE)
public class VirtualRepositoryConfiguration extends RepositoryConfiguration {
	@XmlJavaTypeAdapter(value = RepositoryConfiguration.Reference.Adapter.class)
	@XmlElementWrapper(name = "sources")
	@XmlElement(name = "repository")
	private Set<RepositoryConfiguration> sources;

	private transient VirtualRepository built;

	protected VirtualRepository build() {
		if(built == null) {
			Repositories repositories = Repositories.create();
			for (RepositoryConfiguration source : sources) {
				repositories = source.appendTo(repositories);
			}
			built = VirtualRepository.create(repositories);
		}
		return built;
	}

}
