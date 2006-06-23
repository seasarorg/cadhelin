package org.seasar.cadhelin.velocity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
	SimpleDateFormat format;
	public DateFormat(String format) {
		this.format = new SimpleDateFormat(format);
	}
	public String format(Object date){
		if (date instanceof Date) {
			return format.format((Date) date);
		}
		return date.toString();
	}
}
