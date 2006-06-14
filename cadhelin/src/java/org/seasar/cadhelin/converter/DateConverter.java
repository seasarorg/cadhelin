package org.seasar.cadhelin.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class DateConverter extends AbstractConverter {
	private static final String ERROR_KEY_DATE_REQUIRED = "error.converter.date.required";
	private static final String ERROR_KEY_DATE_FORMAT = "error.converter.date.format";
	private boolean required = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public DateConverter() {
		super(new Object[]{"date",Date.class});
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		DateConverter converter = null;
		try {
			converter = (DateConverter) clone();
			converter.parameterName = parameterName;
			if(validater!=null){
				converter.required = validater.required();				
			}
			return converter;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(str==null || str.length()==0){
			if(required){
				messages.put(parameterName,new Message(ERROR_KEY_DATE_REQUIRED+"."+parameterName));
			}
			return null;
		}
		try {
			return sdf.parse(str);
		} catch (ParseException e1) {
			messages.put(parameterName,new Message(ERROR_KEY_DATE_FORMAT+"."+parameterName));
			return str;
		}
	};

}
