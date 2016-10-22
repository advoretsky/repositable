package org.perfectable.artifactable.metadata;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampAdapter extends XmlAdapter<String, LocalDateTime> {

	static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Override
	public LocalDateTime unmarshal(String s) throws Exception {
		return LocalDateTime.parse(s, TIMESTAMP_FORMATTER);
	}

	@Override
	public String marshal(LocalDateTime d) throws Exception {
		return d.format(TIMESTAMP_FORMATTER);
	}
}
