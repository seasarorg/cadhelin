package org.seasar.cadhelin.velocity;

import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;

public class ClasspathResourceLoader extends
		org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader {
	@Override
	public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
		if(name.startsWith("/")){
			name = name.substring(1);
		}
		return super.getResourceStream(name);
	}
}
