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
* @Description ServletConfig类，每个Servlet实例应持有对应的ServletConfig类
*/
public final class ApplicationServletConfig implements ServletConfig {
	
	public static final String STATIC = "static";
	public static final String SERVLET = "servlet";
	public static final String JSP = "jsp";
	
	//所属的Wrapper容器
	private final Wrapper wrapper;
	
	//Servlet名称，由web.xml文件的servlet-name决定
	String servletName = null;
	
	//
	String servletClass = null;
	
	String servletType;
	
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

	@Override
	public String toString() {
		return "ApplicationServletConfig [servletName=" + servletName + ", servletClass=" + servletClass + "]";
	}
}
