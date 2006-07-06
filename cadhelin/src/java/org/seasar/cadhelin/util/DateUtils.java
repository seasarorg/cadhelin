package org.seasar.cadhelin.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.seasar.cadhelin.velocity.InsertAsIs;

public class DateUtils {
	public int getValue(Object value,int type){
		if (value instanceof Date) {
			Date date = (Date) value;
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			return cal.get(type);
		}
		if (value instanceof Map) {
			Map map = (Map) value;
			Object v = "0";
			if(type==Calendar.YEAR){
				value = map.get("year");
			}else if(type==Calendar.MONTH){
				value = map.get("month");
			}else if(type==Calendar.DATE){
				value = map.get("month");
			}
			if (v instanceof String) {
				return Integer.parseInt((String) v);
				
			}
		}
		return 0;
	}
	public InsertAsIs getYearOption(int start,int end,Object value){
		return getOption(start,end,getValue(value,Calendar.YEAR));
	}
	public InsertAsIs getMonthOption(Object value){
		return getOption(1,12,getValue(value,Calendar.MONTH) + 1);
	}
	public InsertAsIs getDateOption(Object value){
		return getOption(1,31,getValue(value,Calendar.DATE));
	}
	public InsertAsIs getOption(int start,int end,int value){
		StringBuffer buff = new StringBuffer();
		for(int i=start;i<=end;i++){
			buff.append("<option value='");
			buff.append(i);
			buff.append("'");
			if(value == i){
				buff.append(" selected");
			}
			buff.append(" >");
			buff.append(i);
			buff.append("</option>\n");
		}
		return new InsertAsIs(buff.toString());
	}
	public static void main(String[] args) {
		DateUtils utils = new DateUtils();
		System.out.println(utils.getYearOption(2005,2030,new Date()));
		System.out.println(utils.getMonthOption(new Date()));
		System.out.println(utils.getDateOption(new Date()));
	}
}
