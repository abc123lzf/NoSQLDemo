package lzf.webserver.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
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
	
	//Servlet Class类名
	String servletClass = null;
	
	String servletType;
	
	final List<String> urlPatterns = new ArrayList<>(1); 
	
	final Map<String, String> parameterMap = new LinkedHashMap<>();
	
	ApplicationServletConfig(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	/**
	 * @return Servlet名称，由web.xml文件或注解的servlet-name决定
	 */
	@Override
	public String getServletName() {
		return servletName;
	}

	/**
	 * @return 该web应用对应的ServletContext对象
	 */
	@Override
	public ServletContext getServletContext() {
		return ((Context) (wrapper.getParentContainer())).getServletContext();
	}
	
	/**
	 * @param name 初始化参数名
	 * @return 参数值
	 */
	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	/**
	 * @return 初始化参数键名集合迭代器
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * @return 该Servlet类名
	 */
	String getServletClass() {
		return servletClass;
	}

	/**
	 * @param servletClass Servlet类名，由web.xml文件或注解的servlet-class决定
	 */
	void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * @param urlPattern 该Servlet的URL匹配规则
	 */
	void addUrlPatern(String... urlPattern) {
		
		for(String url : urlPattern) {
			if(urlPatterns.contains(url))
				continue;
			
			urlPatterns.add(url);
		}
	}
	
	@Override
	public String toString() {
		return "ApplicationServletConfig [servletName=" + servletName + ", servletClass=" + servletClass + "]";
	}
}
