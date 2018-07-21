package lzf.webserver.core;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import lzf.webserver.Context;
import lzf.webserver.Wrapper;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月21日 下午3:14:36
* @Description 类说明
*/
public final class ApplicationServletConfig implements ServletConfig {
	
	private final Wrapper wrapper;
	
	String servletName = null;
	
	String servletClass = null;
	
	final Map<String, String> parameterMap = new LinkedHashMap<>();
	
	
	ApplicationServletConfig(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public String getServletName() {
		return servletName;
	}

	@Override
	public ServletContext getServletContext() {
		return ((Context) (wrapper.getParentContainer())).getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	String getServletClass() {
		return servletClass;
	}

	void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

}
