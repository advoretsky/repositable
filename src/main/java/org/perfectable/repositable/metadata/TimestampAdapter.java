package org.perfectable.repositable.metadata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

class TimestampAdapter extends XmlAdapter<String, LocalDateTime> {
	private final DateTimeFormatter formatter;

	TimestampAdapter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public LocalDateTime unmarshal(String timestampRepresentation) {
		return LocalDateTime.parse(timestampRepresentation, formatter);
	}

	@Override
	public String marshal(LocalDateTime timestamp) {
		return timestamp.format(formatter);
	}

	public static class WithSeparator extends TimestampAdapter {
		private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss");

		WithSeparator() {
			super(TIMESTAMP_FORMATTER);
		}
	}

	public static class WithoutSeparator extends TimestampAdapter {
		private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		WithoutSeparator() {
			super(TIMESTAMP_FORMATTER);
		}
	}
}
