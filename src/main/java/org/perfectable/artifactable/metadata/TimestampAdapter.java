package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class TimestampAdapter extends XmlAdapter<String, LocalDateTime> {

	private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Override
	public LocalDateTime unmarshal(String timestampRepresentation) {
		return LocalDateTime.parse(timestampRepresentation, TIMESTAMP_FORMATTER);
	}

	@Override
	public String marshal(LocalDateTime timestamp) {
		return timestamp.format(TIMESTAMP_FORMATTER);
	}
}
