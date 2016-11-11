package org.perfectable.repositable.metadata;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.OutputStream;
import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Metadata",
	propOrder = {"groupId", "artifactId", "version", "versioning"}) // plugins are ignored
public class Metadata {
	@SuppressWarnings("unused")
	@XmlElement(name = "groupId")
	private String groupId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "artifactId")
	private String artifactId; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "version")
	private Version version; // NOPMD is read only by JAXB

	@SuppressWarnings("unused")
	@XmlElement(name = "versioning")
	private Versioning versioning = Versioning.create(); // NOPMD is read only by JAXB

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void addVersion(String version) {
		versioning.addVersion(version);
	}

	public void addSnapshotVersion(String classifier, String extension, String version, int buildId,
								   LocalDateTime timestamp) {
		versioning.addSnapshotVersion(classifier, extension, version, buildId, timestamp);
	}

	private void setVersion(Version version) {
		this.version = version;
	}

	public void setVersion(String version) {
		setVersion(Version.of(version));
	}

	private void setVersioning(Versioning versioning) {
		this.versioning = versioning;
	}

	public boolean isEmpty() {
		return versioning.isEmpty();
	}

	public Metadata merge(Metadata other) {
		Metadata result = new Metadata();
		checkArgument(other.groupId.equals(groupId));
		checkArgument(other.artifactId.equals(artifactId));
		result.setGroupId(groupId);
		result.setArtifactId(artifactId);
		Version newVersion = Version.COMPARATOR.compare(other.version, this.version) > 0 ? other.version : version;
		result.setVersion(newVersion);
		Versioning newVersioning = versioning.merge(other.versioning);
		result.setVersioning(newVersioning);
		return result;
	}

	public void writeInto(OutputStream targetStream) {
		try {
			JAXBContext context = JAXBContext.newInstance(Metadata.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, targetStream);
		}
		catch (JAXBException e) {
			throw new AssertionError(e);
		}
	}
}
