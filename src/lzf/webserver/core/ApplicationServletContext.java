package lzf.webserver.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;

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
		attributeMap.put(InstanceManager.class.getName(), new SimpleInstanceManager());
	}
	
	@Override
	public String getContextPath() {
		System.out.println("GET CONTEXT PATH"); //TODO
		
		if(context.getName().equals("ROOT"))
			return "";
		else
			return "/" + context.getName();
	}

	@Override
	public ServletContext getContext(String uripath) {
		
		//为了确保其它Web应用安全性，只能返回null
		return null;
	}

	/**
	 * 此Servlet容器支持的Servlet API版本，符合3.1的返回3，2.4的返回2
	 * @return 3
	 */
	@Override
	public int getMajorVersion() {
		return 3;
	}

	/**
	 * 返回此servlet容器支持的Servlet API的次要版本。 符合版本2.4的所有实现都必须使用此方法返回整数4
	 * @return 1
	 */
	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 2;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 1;
	}

	@Override
	public String getMimeType(String file) {
		return null;
	}

	/**
	 * 给定资源绝对路径返回该目录下的所有资源路径集合
	 * @param path 绝对路径，必须以/开头
	 * @return 该目录下的所有资源路径集合
	 */
	@Override
	public Set<String> getResourcePaths(String path) {
		
		System.out.println("GET Resource PATH"); //TODO
		
		Set<String> set = new TreeSet<>();
		searchDirectory(new File(context.getPath(), path), set);
		
		return set;
	}
	
	private void searchDirectory(File dir, Set<String> set) {
		
		System.out.println("searchDirectory"); //TODO
		
		File[] files = dir.listFiles();
		
		for(File file : files) {
			
			if(file.isDirectory()) {
				searchDirectory(file, set);
			} else {
				set.add(file.getAbsolutePath().replace(context.getPath().getAbsolutePath(), ""));
			}
		}
	}

	/**
	 * @param 资源绝对路径，以/开头
	 * @return 资源URL路径，如果不存在则返回null
	 */
	@Override
	public URL getResource(String path) throws MalformedURLException {
		
		System.out.println("getResource:" + path);
		
		File file = new File(context.getPath(), path);
		
		if(!file.exists())
			return null;
		
		return file.toURI().toURL();
	}
	

	/**
	 * @param 资源绝对路径，以/开头
	 * @return 资源文件输入流，如果不存在该文件则返回null
	 */
	@Override
	public InputStream getResourceAsStream(String path) {
		
		File file = new File(context.getPath(), path);
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(file);
			System.out.println(file.getAbsolutePath());
			return fis;
		} catch (FileNotFoundException e) {
			log.warn("" + file.getAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * @param path 绝对资源路径(可为动态也可为静态)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new ApplicationRequestDispatcher(context, path);
	}

	/**
	 * @param name Servlet名称
	 * @return RequestDispatcher对象，如果无法找到这个Servlet则返回null
	 */
	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		
		Wrapper wrapper = context.getChildContainer(name);
		
		if(wrapper == null)
			return null;
		
		return new ApplicationRequestDispatcher(context, wrapper);
	}

	/**
	 * @param name Servlet名称
	 * @return Servlet对象，如果不存在则返回null
	 */
	@Override @Deprecated
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
	@Override @Deprecated
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

	/**
	 * @return 该Context容器所有Servlet名称的迭代器
	 */
	@Override @Deprecated
	public Enumeration<String> getServletNames() {
		
		List<Wrapper> containerList = context.getChildContainers();
		List<String> list = new LinkedList<>();
		
		for(Wrapper wrapper : containerList) {
			list.add(((StandardWrapper)wrapper).servletConfig.servletName);
		}
		
		return new IteratorEnumeration<String>(list.iterator());
	}

	/**
	 * 将指定的消息写入servlet日志文件，通常是事件日志。 
	 */
	@Override
	public void log(String msg) {
		log.info(msg);
	}

	
	@Override @Deprecated
	public void log(Exception exception, String msg) {
		log.error(msg, exception);
	}

	/**
	 * 将给定Throwable异常的解释性消息和堆栈跟踪写入servlet日志文件,通常是事件日志。
	 */
	@Override
	public void log(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	/**
	 * @param path 资源绝对路径
	 * @return 真实路径，比如http://localhost:9090/path
	 */
	@Override
	public String getRealPath(String path) {
		
		System.out.println("GET REAL PATH:" + path); //TODO
		
		String hostName = context.getParentContainer().getName();
		int port = context.getParentContainer().getParentContainer().getService()
				.getConnectors().get(0).getPort();
		
		String contextName = context.getName();
		String realPath = "http://" + hostName;
		
		if(port != 80) {
			realPath += ":" + port;
		}
		
		if(!contextName.equals("ROOT")) {
			realPath += "/" + contextName;
		}
		
		return realPath += path;
	}

	/**
	 * @return 服务器名、版本等描述信息
	 */
	@Override
	public String getServerInfo() {
		return "APlus-Server/1.0";
	}

	/**
	 * 根据键名获取Context级别的初始化参数
	 * @param name 参数名
	 * @return 参数值
	 */
	@Override
	public String getInitParameter(String name) {
		return parameterMap.get(name);
	}

	/**
	 * @return 所有初始化参数键名集合的迭代器
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		return new IteratorEnumeration<String>(parameterMap.keySet().iterator());
	}

	/**
	 * 添加初始化参数
	 * @param name 键名
	 * @param value 键值
	 * @return 如果添加成功返回true，如果该初始化参数已经存在则返回false
	 */
	@Override
	public boolean setInitParameter(String name, String value) {
		
		if(parameterMap.containsKey(name))
			return false;
		
		parameterMap.put(name, value);
		return true;
	}

	/**
	 * 获取Application作用域的Attribute参数
	 * @param name 参数名
	 * @return 参数值
	 */
	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	/**
	 * @return Application作用域的Attribute参数键名集合的迭代器
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return new IteratorEnumeration<>(attributeMap.keySet().iterator());
	}

	/**
	 * 设置Application作用域的Attribute参数，如果该键已存在会被覆盖
	 * @param name 参数名
	 * @param object 参数值
	 */
	@Override
	public void setAttribute(String name, Object object) {
		attributeMap.put(name, object);
	}

	/**
	 * 移除Application作用域的Attribute参数
	 * @param name 参数名
	 */
	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	/**
	 * 返回该web应用的名称
	 * @return web应用名，默认为web应用的文件夹名
	 */
	@Override
	public String getServletContextName() {
		return context.getName();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, String className) {
		
		StandardWrapper wrapper = (StandardWrapper) StandardWrapper.getDynamicWrapper(context, 
				servletName, className, null, null);
		context.addChildContainer(wrapper);
		
		return new ApplicationServletRegistration(wrapper);
	}

	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
		return null;
	}

	/**
	 * 动态添加Servlet
	 * @param servletName Servlet名称
	 * @param servletClass Servlet类对象
	 */
	@Override
	public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
		StandardWrapper wrapper = (StandardWrapper) StandardWrapper.getDynamicWrapper(context,
				servletName, servletClass.getClass().getName(), null, null);
		context.addChildContainer(wrapper);
		
		return new ApplicationServletRegistration(wrapper);
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

	/**
	 * @return ServletRegistration实现类
	 */
	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return ((StandardWrapper)context.getChildContainer(servletName)).servletRegistration;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		
		Map<String, ServletRegistration> map = new HashMap<>();
		
		for(Wrapper wrapper : context.getChildContainers()) {
			
			ServletRegistration servletRegistration = ((StandardWrapper)wrapper).servletRegistration;
			map.put(servletRegistration.getName(), servletRegistration);
		}
		
		return map;
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, String className) {
		ApplicationFilterConfig config = new ApplicationFilterConfig(context, filterName, className);
		return new ApplicationFilterRegistration(config);
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
		ApplicationFilterConfig config = new ApplicationFilterConfig(context, filterName, filter);
		return new ApplicationFilterRegistration(config);
	}

	@Override
	public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
		ApplicationFilterConfig config = new ApplicationFilterConfig(context, filterName, filterClass);
		return new ApplicationFilterRegistration(config);
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
		ApplicationFilterConfig config = context.filterChain.getFilterConfig(filterName);
		return new ApplicationFilterRegistration(config);
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

	/**
	 * Session追踪方式，有Cookie、URL、SSL
	 * @return 该应用默认的Session追踪方式
	 */
	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		Set<SessionTrackingMode> set = new HashSet<>(1);
		set.add(SessionTrackingMode.COOKIE);
		return set;
	}

	/**
	 * Session追踪方式，有Cookie、URL、SSL
	 * @return 该应用有效的Session追踪方式
	 */
	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		Set<SessionTrackingMode> set = new HashSet<>(1);
		set.add(SessionTrackingMode.COOKIE);
		return set;
	}

	/**
	 * 动态添加监听器
	 * @param className 监听器类名
	 */
	@Override
	public void addListener(String className) {
		context.getListenerContainer().addListenerClass(className);
	}

	/**
	 * 动态添加监听器
	 * @param t 监听器实例
	 */
	@Override
	public <T extends EventListener> void addListener(T t) {
		context.getListenerContainer().addListenerInstance(t);
	}

	/**
	 * 动态添加监听器
	 * @param listenerClass 监听器类对象
	 */
	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		context.getListenerContainer().addListenerClass(listenerClass.getClass().getName());
	}

	/**
	 * 动态添加监听器并绑定
	 * @return 监听器实例
	 */
	@Override
	public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
		
		try {
			T listener = c.newInstance();
			context.getListenerContainer().addListenerInstance(listener);
			return listener;
		} catch (InstantiationException e) {
			throw new ServletException();
		} catch (IllegalAccessException e) {
			throw new ServletException();
		}
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return null;
	}

	/**
	 * @return 该web应用的专用类加载器
	 */
	@Override
	public ClassLoader getClassLoader() {
		System.out.println("getClassLoader"); //TODO
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
