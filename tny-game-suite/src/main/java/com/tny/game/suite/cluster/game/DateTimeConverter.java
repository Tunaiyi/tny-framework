package com.tny.game.suite.cluster.game;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeConverter extends AbstractSingleValueConverter {

	private DateTimeFormatter format;

	public DateTimeConverter(DateTimeFormatter format) {
		this.format = format;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean canConvert(Class clazz) {
		return clazz.isAssignableFrom(DateTime.class);
	}

	@Override
	public Object fromString(String value) {
		return this.format.parseDateTime(value);
	}
}
