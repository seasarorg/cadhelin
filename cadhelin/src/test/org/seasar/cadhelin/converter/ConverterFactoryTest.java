package org.seasar.cadhelin.converter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockServletContextImpl;

public class ConverterFactoryTest extends S2TestCase {
	private ConverterFactory converterFactory;
    public void setUp() {
        include("ConverterFactoryTest.dicon");
    }

	public void testGetConverterClass() throws Exception {
		ServletContext context = new MockServletContextImpl("/Test");
		MockHttpServletRequestImpl request = new MockHttpServletRequestImpl(context, "/test");
		request.addParameter("lhs","123");
//		request.addParameter("rhs","234");
		Map<String, Message> messages = new HashMap<String, Message>();
		Method method = getClass().getMethod("action1",new Class[]{int.class,int.class});
		Converter[] converters = converterFactory.createConverters(method,new String[]{"lhs","rhs"});
		Object[] values = new Object[converters.length];
		int i=0;
		for (Converter converter : converters) {
			values[i] = converter.convert(request,messages);
			i++;
		}
		assertEquals("[123,234]",StringUtil.convertToString(values));
	}
	public int action1(int lhs,int rhs){
		return lhs + rhs;
	}
}
