/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
