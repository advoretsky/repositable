package org.perfectable.artifactable.configuration;

import org.perfectable.artifactable.Repositories;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@XmlType(name = "Repository", propOrder = {"name"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({FileRepositoryConfiguration.class, VirtualRepositoryConfiguration.class})
public abstract class RepositoryConfiguration {
	@XmlID
	@XmlAttribute(name = "name")
	protected String name;

	public abstract Repositories appendTo(Repositories repositories);

	@XmlType(name = "RepositoryConfigurationReference", propOrder = {"repository"})
	public static class Reference {
		@XmlIDREF
		@XmlAttribute(name = "ref", required = true)
		private RepositoryConfiguration repository;

		private Reference() {
			// required for jaxb
		}

		Reference(RepositoryConfiguration repository) {
			this.repository = repository;
		}

		public static class Adapter extends XmlAdapter<Reference, RepositoryConfiguration> {
			@Override
			public RepositoryConfiguration unmarshal(Reference reference) {
				return reference.repository;
			}

			@Override
			public Reference marshal(RepositoryConfiguration repository) {
				return new Reference(repository);
			}
		}
	}
}
