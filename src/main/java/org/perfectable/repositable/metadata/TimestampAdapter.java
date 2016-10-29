package org.perfectable.repositable.metadata;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class TimestampAdapter extends XmlAdapter<String, LocalDateTime> {
	private final DateTimeFormatter formatter;

	public TimestampAdapter(DateTimeFormatter formatter) {
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

		public WithSeparator() {
			super(TIMESTAMP_FORMATTER);
		}
	}

	public static class WithoutSeparator extends TimestampAdapter {
		private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		public WithoutSeparator() {
			super(TIMESTAMP_FORMATTER);
		}
	}
}
