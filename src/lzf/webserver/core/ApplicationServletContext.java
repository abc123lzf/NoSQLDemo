package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import lzf.webserver.Host;
import lzf.webserver.Wrapper;
import lzf.webserver.log.Log;
import lzf.webserver.log.LogFactory;
import lzf.webserver.util.IteratorEnumeration;

/**
* @author 李子帆
* @version 1.0
* @date 2018年7月20日 上午11:54:02
* @Description 此类应包含在Context中
*/
public class ApplicationServletContext implements ServletContext {
	
	private final Log log = LogFactory.getLog(ApplicationServletContext.class);

	//该ServletContext所属的web容器
	private final StandardContext context;
	
	//Application作用域的属性Map
	private final Map<String, Object> attributeMap = new ConcurrentHashMap<>();
	
	private final Map<String, String> parameterMap = new ConcurrentHashMap<>();
	
	public ApplicationServletContext(StandardContext context) {
		this.context = context;
	}
	
	@Override
	public String getContextPath() {
		return context.getPath().getAbsolutePath();
	}

	@Override
	public ServletContext getContext(String uripath) {
		//不要修改，只能返回null
		return null;
	}

	@Override
	public int getMajorVersion() {
		return 3;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 3;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 1;
	}

	@Override
	public String getMimeType(String file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		
		File file = new File(context.getPath(), path);
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(file);
			return fis;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
		
		List<Wrapper> list = context.getChildContainers();
		
		for(Wrapper wrapper : list) {
			if(((StandardWrapper)wrapper).servletConfig.servletName.equals(name))
				return ((StandardWrapper)wrapper).getServlet();
		}
		
		return null;
	}

	/**
	 * @return 该Context容器包含的所有Servlet的迭代器
	 */
	@Override 
	public Enumeration<Servlet> getServlets() {
		
		List<Wrapper> containerList = context.getChildContainers();
		List<Servlet> list = new LinkedList<>();
		
		try {
			for(Wrapper wrapper : containerList) 
				list.add(wrapper.getServlet());
			
			return new IteratorEnumeration<Servlet>(list.iterator());
			
		} catch (ServletException e) {
			
			log.error("", e);
			return null;
		}
	}

	@Override
	public Enumeration<String> getServletNames() {
		
		List<Wrapper> containerList = context.getChildContainers();
		List<String> list = new LinkedList<>();
		
		for(Wrapper wrapper : containerList) {
			list.add(((StandardWrapper)wrapper).servletConfig.servletName);
		}
		
		return new IteratorEnumeration<String>(list.iterator());
	}

	@Override
	public void log(String msg) {
		log.info(msg);
	}

	@Override
	public void log(Exception exception, String msg) {
		log.error(msg, exception);
	}

	@Override
	public void log(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	@Override
	public String getRealPath(String path) {
		File file = new File(context.getPath(), path);
		return file.getAbsolutePath();
	}

	@Override
	public String getServerInfo() {
		return "APlus-Server/1.0";
	}

	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		
		if(parameterMap.containsKey(name))
			return false;
		
		parameterMap.put(name, value);
		return true;
	}

	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<>(attributeMap.keySet().iterator());
	}

	@Override
	public void setAttribute(String name, Object object) {
		attributeMap.put(name, object);
	}

	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	@Override
	public String getServletContextName() {
		return context.getName();
	}

	@Override
	public Dynamic addServlet(String servletName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
		
		try {
			return c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
			return null;
		} catch (IllegalAccessException e) {
			log.error("", e);
			return null;
		}
		
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
		
		try {
			return c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
		}
		
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return context.getSessionCookieConfig();
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(String className) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends EventListener> void addListener(T t) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return context.getWebappLoader().getClassLoader();
	}

	@Override
	public void declareRoles(String... roleNames) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVirtualServerName() {
		return ((Host)context.getParentContainer()).getName();
	}

}
